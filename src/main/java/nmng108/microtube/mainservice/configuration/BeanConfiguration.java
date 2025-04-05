package nmng108.microtube.mainservice.configuration;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import nmng108.microtube.mainservice.dto.auth.LoginRequest;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.reactive.function.client.WebClient;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;

@Configuration
public class BeanConfiguration {
    @Bean
    public JsonMapper jsonMapper() {
        return JsonMapper.builder()
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .addModule(new JavaTimeModule())
                .build();
    }

    @Bean
    @LoadBalanced
    public WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    /**
     * Customize base name, encoding, cache... for message source files.
     * Alternative to the "spring.messages" property set.
     */
    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();

        messageSource.setBasenames("classpath:messages", "classpath:custom-response-status", "classpath:errors", "classpath:error-codes");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(3600); // Cache for an hour

        return messageSource;
    }

//    /**
//     * Configure where to read locale info of a request. By default, Spring reads "Accept-Language" header.
//     */
//    @Bean
//    public LocaleResolver localeResolver() {
////        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
//        CookieLocaleResolver localeResolver = new CookieLocaleResolver("Locale");
//        localeResolver.setDefaultLocale(Locale.US); // Set the default locale if none is specified
//        return localeResolver;
//    }

    @Bean
    public PropertyEditorRegistrar customPropertyEditorRegistrar() {
        PropertyEditor propertyEditor = new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                super.setAsText(text);
            }
        };

        return (registry) -> {
            registry.registerCustomEditor(LoginRequest.class, new StringTrimmerEditor(false));
        };
    }
}
