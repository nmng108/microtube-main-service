package nmng108.microtube.mainservice.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.multipart.DefaultPartHttpMessageReader;
import org.springframework.http.codec.multipart.MultipartHttpMessageReader;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class WebFluxConfiguration implements WebFluxConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        WebFluxConfigurer.super.addCorsMappings(registry);

        registry.addMapping("/**")
                .allowedOrigins("*")
//                .allowCredentials(true)
                .allowedHeaders("*");
    }

//    @Override
//    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
//        var partReader = new DefaultPartHttpMessageReader();
//
//        partReader.setMaxParts(1);
//        partReader.setMaxDiskUsagePerPart(2 * 1024 * 1024 * 1024L);
//        partReader.setEnableLoggingRequestDetails(true);
//
//        MultipartHttpMessageReader multipartReader = new MultipartHttpMessageReader(partReader);
//        multipartReader.setEnableLoggingRequestDetails(true);
//
//        configurer.defaultCodecs().multipartReader(multipartReader);
//    }
}
