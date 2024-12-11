package eu.nebulouscloud.securitymanager;

import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;

@OpenAPIDefinition(
        info = @Info(
                title = "Security Policy Engine Client",
                version = "2.0.1",
                description = "API for managing Security resource policies in Kubernetes.",
                contact = @Contact(
                        name = "Nikos Papageorgopoulos",
                        url = "https://ubitech.eu",
                        email = "npapageorgopoulos@ubitech.eu"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"
                )
        )
)
public class APIConfiguration extends Application {}
