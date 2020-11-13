package de.fraunhofer.isst.dataspaceconnector;

import de.fraunhofer.isst.dataspaceconnector.message.ArtifactMessageHandler;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * This is the main application class. The application is started and an openApi bean for the Swagger UI is created.
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
@SpringBootApplication
@ComponentScan({
        "de.fraunhofer.isst.ids.framework.messaging.spring.controller",
        "de.fraunhofer.isst.ids.framework.messaging.spring",
        "de.fraunhofer.isst.dataspaceconnector",
//        "de.fraunhofer.isst.ids.framework.configurationmanager.controller",
        "de.fraunhofer.isst.ids.framework.spring.starter"
})
public class ConnectorApplication {
    public static final Logger LOGGER = LoggerFactory.getLogger(ConnectorApplication.class);
    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public static void main(String[] args) {
        SpringApplication.run(ConnectorApplication.class, args);
    }

    /**
     * Creates the OpenAPI main description.
     *
     * @return The OpenAPI.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("Dataspace Connector API")
                        .description("This is the Dataspace Connector's backend API using springdoc-openapi and OpenAPI 3.")
                        .version("v3.3.0-SNAPSHOT")
                        .contact(new Contact()
                                .name("Julia Pampus")
                                .email("julia.pampus@isst.fraunhofer.de")
                        )
                        .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0.txt"))
                );
    }

    @PostConstruct
    public void loadAccessUrl() throws UnknownHostException {
        String address = InetAddress.getLocalHost().getHostAddress();
        String name = InetAddress.getLocalHost().getHostName();

        LOGGER.info(address + " " + name);
    }
}
