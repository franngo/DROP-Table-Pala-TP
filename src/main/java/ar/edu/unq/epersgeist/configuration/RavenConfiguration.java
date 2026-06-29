package ar.edu.unq.epersgeist.configuration;

import net.ravendb.client.documents.DocumentStore;
import net.ravendb.client.documents.IDocumentStore;
import net.ravendb.client.documents.conventions.DocumentConventions;
import net.ravendb.client.documents.operations.revisions.ConfigureRevisionsOperation;
import net.ravendb.client.documents.operations.revisions.RevisionsCollectionConfiguration;
import net.ravendb.client.documents.operations.revisions.RevisionsConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class RavenConfiguration {

    @Bean
    public IDocumentStore documentStore(
            @Value("${ravendb.url}") String url,
            @Value("${ravendb.database}") String database
    ) {
        DocumentStore store = new DocumentStore();

        store.setUrls(new String[]{url});
        store.setDatabase(database);

        // Deshabilita las actualizaciones de topología para evitar problemas de conexión en entornos de desarrollo
        store.setConventions(new DocumentConventions() {{
            setDisableTopologyUpdates(true);
        }});

        store.getConventions().setFindIdentityProperty(memberInfo -> Boolean.valueOf("id_de_raven_fantasma"));

        store.initialize();

        // Activa el historial (revisiones) automático para cada colección
        store.maintenance().send(
                new ConfigureRevisionsOperation(
                        new RevisionsConfiguration() {{
                            setCollections(Map.of(
                                    "Espiritus", new RevisionsCollectionConfiguration() {{
                                        setDisabled(false);
                                    }}
                            ));
                        }}
                ));

        return store;
    }
}
