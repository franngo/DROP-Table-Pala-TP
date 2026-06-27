package ar.edu.unq.epersgeist.configuration;

import lombok.Value;
import net.ravendb.client.documents.DocumentStore;
import net.ravendb.client.documents.IDocumentStore;
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
            store.initialize();

            return store;
        }
    }

}
