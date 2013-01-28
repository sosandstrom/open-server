/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.config;

import com.wadpam.open.json.SkipNullObjectMapper;
import com.wadpam.open.service.ComplexService;
import com.wadpam.open.service.ExportService;
import com.wadpam.open.service.SampleService;
import com.wadpam.open.task.PingController;
import com.wadpam.open.web.ComplexController;
import com.wadpam.open.web.ExportController;
import com.wadpam.open.web.MonitorController;
import com.wadpam.open.web.RestJsonExceptionResolver;
import com.wadpam.open.web.SampleController;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 *
 * @author sosandstrom
 */
@EnableWebMvc
@Configuration
@Import(value={ProfileConfig.class})
@ImportResource("classpath:/spring-dao.xml")
public class MvcConfig extends WebMvcConfigurerAdapter {

    // -------------- Services ----------------------
    
    @Bean
    public SampleService sampleService() {
        return new SampleService();
    }
    
    @Bean
    public ComplexService complexService() {
        return new ComplexService();
    }
    
    @Bean(initMethod="init")
    public ExportService exportService() {
        return new ExportService();
    }
    
    // -------------- Message Converters ----------------------

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        SkipNullObjectMapper skipNullMapper = new SkipNullObjectMapper();
        skipNullMapper.init();
        MappingJacksonHttpMessageConverter converter = new MappingJacksonHttpMessageConverter();
        converter.setObjectMapper(skipNullMapper);
        converters.add(converter);
    }
    
    
    // -------------- Serving Resources ----------------------

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("/static/")
                .addResourceLocations("classpath:/static/");
    }
    
    // -------------- Controllers ----------------------
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(brandInterceptor());
//        registry.addInterceptor(trackingCodeInterceptor()).addPathPatterns("/**/isalive");
    }
    
    @Bean
    public MonitorController monitorController() {
        return new MonitorController();
    }
    
    @Bean
    public PingController pingController() {
        return new PingController();
    }
    
    @Bean
    public SampleController sampleController() {
        return new SampleController();
    }

    @Bean
    public ComplexController complexController() {
        return new ComplexController();
    }

    @Bean
    public ExportController exportController() {
        return new ExportController();
    }

    // -------------- View Stuff -----------------------
    
    public @Bean RestJsonExceptionResolver restJsonExceptionResolver() {
        final RestJsonExceptionResolver bean = new RestJsonExceptionResolver();
        bean.setOrder(100);
        return bean;
    }
    
//    public @Bean VelocityConfigurer velocityConfigurer() {
//        final VelocityConfigurer bean = new VelocityConfigurer();
//        bean.setResourceLoaderPath("/WEB-INF/velocity/");
//        return bean;
//    }
//    
//    public @Bean VelocityViewResolver velocityViewResolver() {
//        final VelocityViewResolver bean = new VelocityViewResolver();
//        bean.setOrder(200);
//        bean.setCache(true);
//        bean.setPrefix("");
//        bean.setSuffix(".vm");
//        return bean;
//    }
//    
    public @Bean InternalResourceViewResolver htmlViewResolver() {
        final InternalResourceViewResolver bean = new InternalResourceViewResolver();
        bean.setViewClass(InternalResourceView.class);
        bean.setOrder(999);
        bean.setPrefix("/protected/backoffice/");
        bean.setSuffix("");
        return bean;
    }
    
}
