package nmng108.microtube.mainservice.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import nmng108.microtube.mainservice.util.constant.Routes;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static java.lang.StringTemplate.STR;

@Configuration
@Profile("!prod")
public class SpringDocConfig {
    private static final String securitySchemeName = "Bearer Authentication";

    @Bean
    public GroupedOpenApi usersAndAuthApis() {
        return GroupedOpenApi.builder()
                .group("User Resources and Auth APIs")
                .pathsToMatch(Routes.User.basePath + "/**", Routes.Auth.basePath + "/**")
                .build();
    }

    @Bean
    public GroupedOpenApi otherResources() {
        return GroupedOpenApi.builder()
                .group("All Resources")
                .pathsToMatch("/**")
                .build();
    }


    @Bean
    public OpenAPI springOpenAPI(@Value("${server.port}") int port) {
        return new OpenAPI()
                .info(new Info().title("Administration Service")
                        .description("List of APIs for integration")
                        .version("v1.0.0"))
//                .externalDocs(new ExternalDocumentation()
//                                .description("Admin API")
//                        .url(STR."http://localhost:\{port}/openapi"))
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                .components(new Components().addSecuritySchemes(
                        securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("Bearer")
                                .bearerFormat("JWT")
                ));
    }


}
