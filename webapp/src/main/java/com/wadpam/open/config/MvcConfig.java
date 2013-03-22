/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.config;

import com.wadpam.open.json.SkipNullObjectMapper;
import com.wadpam.open.security.InMemorySecurityDetailsService;
import com.wadpam.open.security.SecurityInterceptor;
import com.wadpam.open.service.ComplexService;
import com.wadpam.open.service.ExportService;
import com.wadpam.open.service.SampleService;
import com.wadpam.open.task.PingController;
import com.wadpam.open.web.ComplexController;
import com.wadpam.open.web.ExportController;
import com.wadpam.open.web.MonitorController;
import com.wadpam.open.web.RestJsonExceptionResolver;
import com.wadpam.open.web.SampleController;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                .addResourceLocations("classpath:/static");
    }

    // -------------- Controllers ----------------------
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(securityInterceptor());
//        registry.addInterceptor(trackingCodeInterceptor()).addPathPatterns("/**/isalive");
    }
    
    @Bean
    public SecurityInterceptor securityInterceptor() {
        SecurityInterceptor bean = new SecurityInterceptor();
        
        InMemorySecurityDetailsService inMem = new InMemorySecurityDetailsService();
        inMem.DETAILS_MAP.put("itest", "itest");
        bean.setSecurityDetailsService(inMem);
        
        // white list most of it
        Collection<Entry<String, Collection<String>>> whitelist = new ArrayList<Entry<String, Collection<String>>>();
        whitelist.add(new AbstractMap.SimpleImmutableEntry<String, Collection<String>>(
                "\\A/domain/itest/export/", Arrays.asList("GET", "POST")));
        whitelist.add(new AbstractMap.SimpleImmutableEntry<String, Collection<String>>(
                "\\A/domain/itest/sample/", Arrays.asList("GET", "POST", "DELETE")));
        whitelist.add(new AbstractMap.SimpleImmutableEntry<String, Collection<String>>(
                "\\A/domain/itest/complex/", Arrays.asList("GET", "POST", "DELETE")));
        whitelist.add(new AbstractMap.SimpleImmutableEntry<String, Collection<String>>(
                "\\A/domain/monitor", Arrays.asList("GET", "POST")));
//        whitelist.add(new AbstractMap.SimpleImmutableEntry<String, Collection<String>>(
//                "", Arrays.asList("GET", "POST")));
        bean.setWhitelistedMethods(whitelist);
                
        return bean;
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
    
    public @Bean InternalResourceViewResolver htmlViewResolver() {
        final InternalResourceViewResolver bean = new InternalResourceViewResolver();
        bean.setViewClass(InternalResourceView.class);
        bean.setOrder(999);
        bean.setPrefix("/internal/");
        bean.setSuffix("");
        return bean;
    }
    
}
