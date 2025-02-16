//package nmng108.microtube.mainservice.configuration;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.format.FormatterRegistry;
//import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
////@EnableWebMvc
//public class WebMvcConfiguration implements WebMvcConfigurer {
//
////    @Override
////    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
////        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder()
////                .indentOutput(true)
//////                .dateFormat(new SimpleDateFormat("yyyy-MM-dd"))
////                .modulesToInstall(new ParameterNamesModule());
////        converters.add(new MappingJackson2HttpMessageConverter(builder.build()));
////        converters.add(new MappingJackson2XmlHttpMessageConverter(builder.createXmlMapper(true).build()));
////    }
//
//    @Override
//    public void addFormatters(FormatterRegistry registry) {
//        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
//        registrar.setUseIsoFormat(true);
//        registrar.registerFormatters(registry);
//    }
//
////    @Override
////    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
//////        WebMvcConfigurer.super.addArgumentResolvers(resolvers);
////        resolvers.add(sortResolver());
////        resolvers.add(pageableResolver());
////    }
////
////    public SortHandlerMethodArgumentResolver sortResolver() {
////        return new SortHandlerMethodArgumentResolver() {
////            @Override
////            @NonNull
////            public Sort resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
////                System.out.println("Run custom sort resolver!");
////                return super.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
////            }
////        };
////    }
////
////    public PageableHandlerMethodArgumentResolver pageableResolver() {
////        return new PageableHandlerMethodArgumentResolver() {
////            @Override
////            @NonNull
////            public Pageable resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
////                System.out.println("Run custom pageable resolver!");
////                return super.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
////            }
////        };
////    }
//}
