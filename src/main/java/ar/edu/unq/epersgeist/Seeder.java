package ar.edu.unq.epersgeist;

import com.mongodb.client.MongoCollection;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Seeder de datos para MongoDB. Se ejecuta al levantar la aplicación y puebla
 * las colecciones configuradas con documentos de prueba.
 * <p>
 * Cada colección se seedea solo si está vacía, por lo que es seguro dejar
 * este componente activo en entornos de desarrollo sin perder datos existentes.
 */
@Component
@RequiredArgsConstructor
public class Seeder implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;

    private static final int BATCH_SIZE = 5_000;
    static final Random RANDOM = new Random();

    private static final String[] UBICACIONES = {
            "Cementerio del Norte", "Santuario de la Luz", "Bosque Arcano",
            "Catedral Oscura", "Ruinas del Sur", "Capilla del Alba",
            "Cripta Olvidada", "Templo Eterno", "Caverna Maldita", "Jardín Sagrado"
    };


    @Override
    public void run(String... args) {
        createDocumentsForCollection("datos_sensores", 500_000, this::generateLogDocument);
    }

    /**
     * Puebla una colección con {@code total} documentos generados por {@code generator},
     * insertando en batches de {@value BATCH_SIZE} elementos para mantener el uso de
     * memoria acotado. Si la colección ya contiene documentos, se omite silenciosamente.
     *
     * @param collection nombre de la colección destino en MongoDB
     * @param total      cantidad de documentos a insertar
     * @param generator  función invocada una vez por documento que produce el {@link Document} a insertar
     */
    private void createDocumentsForCollection(String collection, int total, Supplier<Document> generator) {
        MongoCollection<Document> col = mongoTemplate.getCollection(collection);

        System.out.println("[Seeder] Limpiando colección " + collection + "...");
        col.deleteMany(new Document());

        List<Document> batch = new ArrayList<>(BATCH_SIZE);

        for (int i = 0; i < total; i++) {
            batch.add(generator.get());

            if (batch.size() == BATCH_SIZE) {
                col.insertMany(batch);
                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            col.insertMany(batch);
        }

        System.out.println("[Seeder] " + total + " documentos insertados en " + collection);
    }

    private Document generateLogDocument() {
        return switch (RANDOM.nextInt(4)) {
            case 0 -> generateSonidoDocument();
            case 1 -> generateTemperaturaDocument();
            case 2 -> generatePresionDocument();
            default -> generateDesconocidoDocument();
        };
    }

    private Document generateSonidoDocument() {
        return new Document()
                .append("ubicacion", randomUbicacion())
                .append("sensor_id", RANDOM.nextInt(21))
                .append("decibeles", 30f + RANDOM.nextFloat() * 120f)
                .append("duracion", 100 + RANDOM.nextInt(9901))
                .append("fecha", randomFecha());
    }

    private Document generateTemperaturaDocument() {
        return new Document()
                .append("ubicacion", randomUbicacion())
                .append("sensor_id", RANDOM.nextInt(21))
                .append("temperatura", new Document()
                        .append("grados", -20f + RANDOM.nextFloat() * 60f)
                        .append("unidad", "°C"))
                .append("fecha", randomFecha());
    }

    private Document generatePresionDocument() {
        return new Document()
                .append("ubicacion", randomUbicacion())
                .append("sensor_id", RANDOM.nextInt(21))
                .append("presionAtmosferica", 950 + RANDOM.nextInt(101))
                .append("fecha", randomFecha());
    }

    private Document generateDesconocidoDocument() {
        return new Document()
                .append("ubicacion", randomUbicacion())
                .append("sensor_id", null)
                .append("l3kTUr4", generateEncryptedString())
                .append("fecha", randomFecha());
    }

    private String randomUbicacion() {
        return UBICACIONES[RANDOM.nextInt(UBICACIONES.length)];
    }

    private Date randomFecha() {
        long thirtyDaysMs = 30L * 24 * 60 * 60 * 1000;
        return new Date(System.currentTimeMillis() - (long) (RANDOM.nextDouble() * thirtyDaysMs));
    }

    private String generateEncryptedString() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(64);
        for (int i = 0; i < 64; i++) {
            sb.append(chars.charAt(RANDOM.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
