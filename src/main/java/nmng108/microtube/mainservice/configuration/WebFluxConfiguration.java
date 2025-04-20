package nmng108.microtube.mainservice.configuration;

import nmng108.microtube.mainservice.dto.video.request.SearchVideoDTO;
import nmng108.microtube.mainservice.util.converter.WebParamConverters;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class WebFluxConfiguration implements WebFluxConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        WebFluxConfigurer.super.addCorsMappings(registry);

        // If not using Gateway, this should be set properly
//        registry.addMapping("/**")
//                .allowedOriginPatterns("http://localhost**", "http://127.0.0.1**")
//                .allowedMethods("*")
//                .allowedHeaders("*")
//                .allowCredentials(true);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        WebFluxConfigurer.super.addFormatters(registry);

        registry.addConverter(new WebParamConverters.SearchVideoDTOSortedFieldConverter());
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
