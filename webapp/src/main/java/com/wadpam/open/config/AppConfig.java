/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.config;

import com.wadpam.open.jsonp.JsonpCallbackFilter;
import com.wadpam.open.web.DomainNamespaceFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author sosandstrom
 */
@Configuration
public class AppConfig {

    @Bean
    public DomainNamespaceFilter domainNamespaceFilter() {
        return new DomainNamespaceFilter();
    }
    
    @Bean
    public JsonpCallbackFilter jsonpFilter() {
        return new JsonpCallbackFilter();
    }
}
