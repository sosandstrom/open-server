/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.config;

import com.wadpam.open.dao.DAppDomainDao;
import com.wadpam.open.dao.DAppDomainDaoBean;
import com.wadpam.open.service.DomainService;
import com.wadpam.open.web.DomainController;
import com.wadpam.open.web.DomainInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * DomainInterceptor should be configured in application.
 * @author sosandstrom
 */
@Configuration
public class DomainConfig {

    @Bean
    public DAppDomainDao dAppDomainDao() {
        return new DAppDomainDaoBean();
    }
    
    @Bean
    public DomainService domainService() {
        DomainService bean = new DomainService();
        bean.setDomainDao(dAppDomainDao());
        return bean;
    }
    
    @Bean
    public DomainController domainController() {
        DomainController bean = new DomainController();
        bean.setDomainService(domainService());
        return bean;
    }
    
    @Bean
    public DomainInterceptor domainInterceptor() {
        DomainInterceptor bean = new DomainInterceptor();
        bean.setDomainService(domainService());
        return bean;
    }
}
