/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.user.config;

import com.wadpam.open.mvc.CrudController;
import com.wadpam.open.mvc.CrudService;
import com.wadpam.open.user.dao.DOpenUserDao;
import com.wadpam.open.user.dao.DOpenUserDaoBean;
import com.wadpam.open.user.service.OpenUserService;
import com.wadpam.open.user.web.OpenUserController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author sosandstrom
 */
@Configuration
public class UserConfig {

    @Bean
    public OpenUserController openUserController() {
        return new OpenUserController();
    }
    
    @Bean
    public OpenUserService openUserService() {
        return new OpenUserService();
    }
    
    @Bean
    public DOpenUserDao dOpenUserDao() {
        return new DOpenUserDaoBean();
    }
    
}
