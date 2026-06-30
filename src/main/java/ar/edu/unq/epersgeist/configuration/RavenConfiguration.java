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

        // --------------------------------------------------------------------------------
        // Deshabilita las actualizaciones de topología para evitar problemas de conexión en entornos de desarrollo
        // --------------------------------------------------------------------------------
        
        store.setConventions(new DocumentConventions() {{
            setDisableTopologyUpdates(true);
        }});

        // --------------------------------------------------------------------------------
        // Deshabilita las actualizaciones de topología para evitar problemas de conexión en entornos de desarrollo
        // --------------------------------------------------------------------------------

        store.getConventions().setFindIdentityProperty(
                memberInfo -> Boolean.valueOf("id_de_raven_fantasma")
        );

        store.initialize();

        // --------------------------------------------------------------------------------
        // Activa las revisiones para ABSOLUTAMENTE TODAS las colecciones creadas
        // --------------------------------------------------------------------------------

        store.maintenance().send(
                new ConfigureRevisionsOperation(
                        new RevisionsConfiguration() {{
                            setDefaultConfig(new RevisionsCollectionConfiguration() {{
                                setDisabled(false);
                                setMinimumRevisionsToKeep(25L);
                            }});
                        }}
                ));

        // --------------------------------------------------------------------------------

        return store;
    }
}
