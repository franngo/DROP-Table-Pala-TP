package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.Coordenada;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.HarmonicResult;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.modelo.Ubicacion.UbicacionMongo;
import ar.edu.unq.epersgeist.modelo.Ubicacion.UbicacionNeo4j;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.NoTodasLasUbicacionesExistenException;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.UbicacionNoEncontradaException;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.UbicacionYaExisteException;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.UbicacionesNoConectadasException;
import ar.edu.unq.epersgeist.modelo.medium.Medium;
import ar.edu.unq.epersgeist.persistencia.mongo.UbicacionDAOMongo;
import ar.edu.unq.epersgeist.persistencia.neo.UbicacionDAONeo4j;
import ar.edu.unq.epersgeist.persistencia.sql.UbicacionDAOSQL;
import ar.edu.unq.epersgeist.servicios.UbicacionService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

// --------------------------------------------------------------------------------

@Service
@Transactional
@AllArgsConstructor

// --------------------------------------------------------------------------------

public class UbicacionServiceImpl implements UbicacionService {
    private final UbicacionDAOSQL ubicacionDAOSQL;
    private final UbicacionDAONeo4j ubicacionDAONeo;
    private final UbicacionDAOMongo ubicacionDAOMongo;
    private final MongoTemplate mongoTemplate;

    // --------------------------------------------------------------------------------
    // CREAR
    // --------------------------------------------------------------------------------

    @Override
    public Ubicacion crear(Ubicacion ubicacion, List<Coordenada> coordenadas) {
        // Busco si ya existe una ubicación con el mismo nombre
        Ubicacion existente = ubicacionDAOSQL.findByNombre(ubicacion.getNombre());

        // Si existe y NO está eliminada lógicamente, entonces no se puede crear otra con el mismo nombre
        if (existente != null && !existente.getDeletedAt()) {
            throw new UbicacionYaExisteException(ubicacion.getNombre());
        }

        // Si existe pero estaba soft deleted, la elimino físicamente para liberar el unique del nombre
        if (existente != null) {
            ubicacionDAOSQL.delete(existente);
            ubicacionDAOSQL.flush(); // Ejecuta el DELETE antes del INSERT
        }

        // Guardo la ubicación en SQL
        ubicacionDAOSQL.save(ubicacion);

        // La persisto también en Neo4j usando el id generado en SQL
        ubicacionDAONeo.save(new UbicacionNeo4j(ubicacion));

        //La persisto también en Mongo
        ubicacionDAOMongo.save(new UbicacionMongo(ubicacion, coordenadas));

        return ubicacion;
    }

    // --------------------------------------------------------------------------------
    // RECUPERAR
    // --------------------------------------------------------------------------------

    @Override
    public Ubicacion recuperar(Long ubicacionId) {
        return ubicacionDAOSQL
                .findById(ubicacionId)
                .orElseThrow(() -> new UbicacionNoEncontradaException(ubicacionId));
    }

    @Override
    public List<Ubicacion> recuperarTodos() {
        return ubicacionDAOSQL.findAll();
    }

    // --------------------------------------------------------------------------------
    // ACTUALIZAR
    // --------------------------------------------------------------------------------

    @Override
    public void actualizar(Ubicacion ubicacion) {
        Long id = ubicacion.getId();

        if (id == null || !ubicacionDAOSQL.existsById(id)) {
            throw new UbicacionNoEncontradaException(id);
        }

        ubicacionDAOSQL.save(ubicacion);
    }

    // --------------------------------------------------------------------------------
    // ELIMINAR
    // --------------------------------------------------------------------------------

    @Override
    public void eliminar(Long id) {
        ubicacionDAOSQL.findById(id).ifPresent(ubicacion -> {
            ubicacion.setDeletedAt(true);
            ubicacionDAOSQL.save(ubicacion);
            ubicacionDAONeo.deleteById(id); //hace un DETACH DELETE del nodo en la DB Neo4j con dicho id
            ubicacionDAOMongo.deleteById(id);
        });
    }

    @Override
    public void eliminarTodos() {
        ubicacionDAOSQL.deleteAll();
        ubicacionDAONeo.deleteAll();
        ubicacionDAOMongo.deleteAll();
    }

    // --------------------------------------------------------------------------------
    // CONSULTAS DE RELACIONES
    // --------------------------------------------------------------------------------

    @Override
    public List<Espiritu> espiritusEn(Long ubicacionId) {
        return ubicacionDAOSQL.espiritusEn(ubicacionId);
    }

    @Override
    public List<Medium> mediumsSinEspiritusEn(Long ubicacionId) {
        return ubicacionDAOSQL.mediumsSinEspiritusEn(ubicacionId);
    }

    // --------------------------------------------------------------------------------
    // MÉTODOS DE CONEXIONES ENTRE UBICACIONES
    // --------------------------------------------------------------------------------

    @Override
    public void conectar(Long idOrigen, Long idDestino, Long costo) {
        this.recuperar(idOrigen);
        this.recuperar(idDestino);

        ubicacionDAONeo.conectar(idOrigen, idDestino, costo);
    }

    // --------------------------------------------------------------------------------

    @Override
    public Boolean estanConectadas(Long idOrigen, Long idDestino) {
        this.recuperar(idOrigen);
        this.recuperar(idDestino);

        return ubicacionDAONeo.estanConectadas(idOrigen, idDestino);
    }

    // --------------------------------------------------------------------------------

    @Override
    public List<Ubicacion> caminoMasRentable(Long idOrigen, Long idDestino) {
        Ubicacion origen = this.recuperar(idOrigen);
        this.recuperar(idDestino);

        //caso borde (si origen y destino son la misma Ubicacion)
        if (idOrigen.equals(idDestino)) {
            return List.of(origen);
        }

        List<UbicacionNeo4j> ubis = ubicacionDAONeo.caminoMasRentable(idOrigen, idDestino);

        if (ubis.isEmpty()) {
            throw new UbicacionesNoConectadasException(idOrigen, idDestino);
        }

        return ubis.stream().map((u) -> this.recuperar(u.getId())).toList();
    }

    // --------------------------------------------------------------------------------

    @Override
    public List<Ubicacion> caminoMasCorto(Long idOrigen, Long idDestino) {
        // 1. Valido que existan ambas ubicaciones
        Ubicacion origen = recuperar(idOrigen);
        recuperar(idDestino);

        // 2. Caso borde: mismo nodo (shortestPath no soporta origen = destino)
        if (idOrigen.equals(idDestino)) {
            return List.of(origen);
        }

        // 3. Obtengo el camino más corto en Neo4j
        List<UbicacionNeo4j> caminoNeo = ubicacionDAONeo.caminoMasCorto(idOrigen, idDestino);

        // 4. Si no hay camino, tiro excepción
        if (caminoNeo.isEmpty()) {
            throw new UbicacionesNoConectadasException(idOrigen, idDestino);
        }

        // 5. Convierto las ubicaciones de Neo4j a las de SQL (para devolver el mismo tipo que el servicio)
        return caminoNeo.stream()
                .map(u -> recuperar(u.getId()))
                .toList();
    }

    // --------------------------------------------------------------------------------

    @Override
    public List<Ubicacion> ubicacionesSobrecargadas(int umbralDeEnergia) {
        return ubicacionDAOSQL.ubicacionSobrecargada(umbralDeEnergia);
    }

    // --------------------------------------------------------------------------------

    @Override
    public List<HarmonicResult> harmonicResult(List<Long> ids) {
        //validación de la existencia de Ubicaciones
//        ids.stream().forEach((id) -> this.recuperar(id));
        List<Ubicacion> ubisExistentes = ubicacionDAOSQL.findAllById(ids);
        if (ubisExistentes.size() != ids.size()) {
            throw new NoTodasLasUbicacionesExistenException();
        }

        return ubicacionDAONeo.harmonicResult(ids);
    }

    // --------------------------------------------------------------------------------
    // CONSULTAS MONGO
    // --------------------------------------------------------------------------------

    @Override
    public List<String> ubicacionesConflictivas() {
        // 1. Defino el valor mínimo de decibeles para considerar un evento como exorcismo.
        final double UMBRAL_DECIBELES = 90.0;

        // 2. Creo una consulta vacía que luego será configurada con criterios de búsqueda.
        Query query = new Query();

        // 3. Agrego la condición de que el campo "decibeles" sea mayor al umbral definido.
        query.addCriteria(
                Criteria.where("decibeles").gt(UMBRAL_DECIBELES)
        );

        // 4. Ejecuto la consulta sobre la colección "datos_sensores".
        List<Document> resultados =
                mongoTemplate.find(query, Document.class, "datos_sensores");

        // 5. Convierto las ubicaciones de los documentos obtenidos a una lista de strings.
        return resultados.stream()
                .map(doc -> doc.getString("ubicacion"))
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    // --------------------------------------------------------------------------------

    @Override
    public List<String> ubicacionesFrias() {

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(
                        Criteria.where("temperatura.grados").exists(true)
                ),
                Aggregation.group("ubicacion")
                        .avg("temperatura.grados")
                        .as("promedio"),
                Aggregation.match(
                        Criteria.where("promedio").lt(5)
                )
        );

        AggregationResults<Document> resultados =
                mongoTemplate.aggregate(
                        aggregation,
                        "datos_sensores",
                        Document.class
                );

        return resultados.getMappedResults()
                .stream()
                .map(doc -> doc.getString("_id"))
                .toList();
    }

    // --------------------------------------------------------------------------------

    @Override
    public Map<String, Integer> sucesos() {

        final double tope_decibeles = 90.0;
        final double tope_temperatura = 5.0;

        final AggregationExpression existeTemperatura = context ->
                new Document("$ne",
                        List.of(
                                new Document("$type", "$temperatura"),
                                "missing"
                        ));
        //o sea, { temperatura: { $exists: true} }

        final AggregationExpression temperaturaMenorAlTope = context ->
                new Document(
                        "$lt",
                        List.of(
                                "$temperatura.grados",
                                tope_temperatura
                        )
                );
        //o sea, { "temperatura.grados": { $lt: 5.0} }

        final AggregationExpression condicionTemperatura =
                BooleanOperators.And.and(
                        existeTemperatura,
                        temperaturaMenorAlTope
                );
        //tengo que preguntar si existe el campo tambien porque el $lt, al comparar un null con un número, entiende que
        // el null es valor númerico menor y da por cumplida la condición. Esto no pasa con el gt.

        final AggregationExpression existeLectura = context ->
                new Document("$ne",
                        List.of(
                                new Document("$type", "$l3kTUr4"),
                                "missing"
                        ));
        //o sea, { $l3kTUr4: { $exists: true} }

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group() //agrupo todos los docs en un mismo grupo para poder emplear agg functions sobre estos
                        .sum(ConditionalOperators
                                .when(
                                        Criteria.where("decibeles").gt(tope_decibeles)
                                )
                                .then(1)
                                .otherwise(0))
                        .as("exorcismos")
                        .sum(ConditionalOperators
                                .when(condicionTemperatura)
                                .then(1)
                                .otherwise(0))
                        .as("invocaciones_demoniacas")
                        .sum(ConditionalOperators
                                .when(existeLectura)
                                .then(1)
                                .otherwise(0))
                        .as("sucesos_desconocidos")
        );

        AggregationResults<Document> resultados =
                mongoTemplate.aggregate(
                        aggregation,
                        "datos_sensores",
                        Document.class
                );

        Document resultado = resultados.getUniqueMappedResult();

        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("exorcismos", resultado.getInteger("exorcismos"));
        map.put("invocaciones demoniacas", resultado.getInteger("invocaciones_demoniacas"));
        map.put("sucesos desconocidos", resultado.getInteger("sucesos_desconocidos"));

        return map;
    }

    // --------------------------------------------------------------------------------
    @Override
    public void normalize() {
        final double tope_decibeles = 90.0;
        final double tope_temperatura = 5.0;

        AggregationOperation etapaProyeccion = context -> Document.parse(String.format("""
        {
          "$project": {
            "_id": "$_id",
            "ubicacion": "$ubicacion",
            "sensor_id": "$sensor_id",
            "fecha": "$fecha",
            "medicion": {
              "$switch": {
                "branches": [
                  { "case": { "$ne": [{ "$type": "$decibeles" }, "missing"] }, "then": "sonido" },
                  { "case": { "$ne": [{ "$type": "$temperatura" }, "missing"] }, "then": "temperatura" },
                  { "case": { "$ne": [{ "$type": "$presionAtmosferica" }, "missing"] }, "then": "presión" }
                ],
                "default": "desconocido"
              }
            },
            "evento": {
              "$cond": [
                { "$gt": ["$decibeles", %f] }, "Exorcismo",
                { "$cond": [
                  { "$and": [
                    { "$ne": [{ "$type": "$temperatura" }, "missing"] },
                    { "$lt": ["$temperatura.grados", %f] }
                  ]}, "Invocación Demoníaca",
                  { "$cond": [
                    { "$ne": [{ "$type": "$l3kTUr4" }, "missing"] }, "Suceso Desconocido",
                    { "$cond": [ { "$ne": [{ "$type": "$presionAtmosferica" }, "missing"] }, "Presencia de Demonios", "Lectura Normal" ] }
                  ]}
                ]}
              ]
            },
            "resultado_sensor": {
              "$switch": {
                "branches": [
                  { "case": { "$ne": [{ "$type": "$decibeles" }, "missing"] }, "then": { "valor": "$decibeles", "medida": "dB" } },
                  { "case": { "$ne": [{ "$type": "$temperatura" }, "missing"] }, "then": { "valor": "$temperatura.grados", "medida": "$temperatura.unidad" } },
                  { "case": { "$ne": [{ "$type": "$presionAtmosferica" }, "missing"] }, "then": { "valor": "$presionAtmosferica", "medida": "hPa" } }
                ],
                "default": { "valor": null, "medida": "N/A" }
              }
            }
          }
        }
        """, tope_decibeles, tope_temperatura));

        AggregationOperation etapaSalida = context -> new Document("$out", "datos_normalizados");

        Aggregation aggregation = Aggregation.newAggregation(etapaProyeccion, etapaSalida);
        mongoTemplate.aggregate(aggregation, "datos_sensores", Document.class);
    }


}
