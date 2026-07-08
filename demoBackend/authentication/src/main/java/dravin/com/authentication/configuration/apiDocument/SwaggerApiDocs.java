package dravin.com.authentication.configuration.apiDocument;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Demo Application build by Dravin Palekar",
                description = "Demo Application of chatting application",
                contact = @Contact(
                        name = "Dravin Palekar",
                        url = "www.dravinpalekar.com",
                        email = "dravin.palekar@gmail.com"
                ),
                version = "0.0.1-SNAPSHOT",
                summary = "Demo Application of chatting application like real world"
        ),
        security = {
                @SecurityRequirement(
                        name = "bearerAuth"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SwaggerApiDocs {
}
