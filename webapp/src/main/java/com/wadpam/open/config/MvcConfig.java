/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.config;

import com.wadpam.oauth2.web.OAuth2Interceptor;
import com.wadpam.open.json.SkipNullObjectMapper;
import com.wadpam.open.security.InMemorySecurityDetailsService;
import com.wadpam.open.security.RolesInterceptor;
import com.wadpam.open.security.SecurityDetailsService;
import com.wadpam.open.security.SecurityInterceptor;
import com.wadpam.open.service.ComplexService;
import com.wadpam.open.service.DomainService;
import com.wadpam.open.service.ExportService;
import com.wadpam.open.service.ItestService;
import com.wadpam.open.service.SampleService;
import com.wadpam.open.task.PingController;
import com.wadpam.open.web.BasicAuthenticationInterceptor;
import com.wadpam.open.web.ComplexController;
import com.wadpam.open.web.ExportController;
import com.wadpam.open.web.MonitorController;
import com.wadpam.open.web.RestJsonExceptionResolver;
import com.wadpam.open.web.SampleController;
import com.wadpam.open.web.SecurityController;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
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
@Import(DomainConfig.class)
@ImportResource("classpath:/spring-dao.xml")
public class MvcConfig extends WebMvcConfigurerAdapter {

    // -------------- Services ----------------------
    
    @Bean(initMethod = "init")
    public ItestService itestService() {
        return new ItestService();
    }
    
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

    // -------------- Interceptors ----------------------
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(basicAuthenticationInterceptor());
        registry.addInterceptor(rolesInterceptor());
        registry.addInterceptor(oauth2Interceptor()).addPathPatterns("/itest/**");
//        registry.addInterceptor(trackingCodeInterceptor()).addPathPatterns("/**/isalive");
    }
    
    @Bean
    public BasicAuthenticationInterceptor basicAuthenticationInterceptor() {
        BasicAuthenticationInterceptor bean = new BasicAuthenticationInterceptor();
        
        // white list most of it
        Collection<Entry<String, Collection<String>>> whitelist = new ArrayList<Entry<String, Collection<String>>>();
        whitelist.add(new AbstractMap.SimpleImmutableEntry<String, Collection<String>>(
                "\\A/api/itest/export/", Arrays.asList("GET", "POST")));
        whitelist.add(new AbstractMap.SimpleImmutableEntry<String, Collection<String>>(
                "\\A/api/itest/sample/", Arrays.asList("GET", "POST", "DELETE")));
        whitelist.add(new AbstractMap.SimpleImmutableEntry<String, Collection<String>>(
                "\\A/api/itest/complex/", Arrays.asList("GET", "POST", "DELETE")));
        whitelist.add(new AbstractMap.SimpleImmutableEntry<String, Collection<String>>(
                "\\A/api/monitor", Arrays.asList("GET", "POST")));
        bean.setWhitelistedMethods(whitelist);
                
        return bean;
    }
    
    @Bean 
    public RolesInterceptor rolesInterceptor() {
        RolesInterceptor bean = new RolesInterceptor();
        
        Collection<Entry<String, Collection<String>>> rules = new ArrayList<Entry<String, Collection<String>>>();
        rules.add(new AbstractMap.SimpleImmutableEntry<String, Collection<String>>(
                "\\AGET:/api/itest/security", Arrays.asList(SecurityDetailsService.ROLE_USER, SecurityDetailsService.ROLE_APPLICATION)));
        rules.add(new AbstractMap.SimpleImmutableEntry<String, Collection<String>>(
                "\\A[^:]+:/api/itest/security", Arrays.asList(SecurityDetailsService.ROLE_USER)));
        rules.add(new AbstractMap.SimpleImmutableEntry<String, Collection<String>>(
                "\\A[^:]+:/api/", SecurityInterceptor.ROLES_ANONYMOUS));
        bean.setRuledMethods(rules);
        
        return bean;
    }
    
    @Bean
    public OAuth2Interceptor oauth2Interceptor() {
        OAuth2Interceptor bean = new OAuth2Interceptor();
        
        // white list most of it
        Collection<Entry<String, Collection<String>>> whitelist = new ArrayList<Entry<String, Collection<String>>>();
        whitelist.add(new AbstractMap.SimpleImmutableEntry<String, Collection<String>>(
                "\\A/api/itest/export/", Arrays.asList("GET", "POST")));
        whitelist.add(new AbstractMap.SimpleImmutableEntry<String, Collection<String>>(
                "\\A/api/itest/sample/", Arrays.asList("GET", "POST", "DELETE")));
        whitelist.add(new AbstractMap.SimpleImmutableEntry<String, Collection<String>>(
                "\\A/api/itest/complex/", Arrays.asList("GET", "POST", "DELETE")));
        whitelist.add(new AbstractMap.SimpleImmutableEntry<String, Collection<String>>(
                "\\A/api/monitor", Arrays.asList("GET", "POST")));
        whitelist.add(new AbstractMap.SimpleImmutableEntry<String, Collection<String>>(
                "\\A/api/itest/security", Arrays.asList("GET")));
        bean.setWhitelistedMethods(whitelist);
        
        
        return bean;
    }
    
    // -------------- Controllers ----------------------
    
    @Bean
    public MonitorController monitorController() {
        return new MonitorController();
    }
    
    @Bean
    public SecurityController securityController() {
        return new SecurityController();
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
