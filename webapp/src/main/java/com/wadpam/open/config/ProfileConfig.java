/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.config;

import com.wadpam.open.dao.DProfileDaoBean;
import com.wadpam.open.mvc.CrudController;
import com.wadpam.open.mvc.CrudService;
import com.wadpam.open.service.ProfileService;
import com.wadpam.open.user.config.UserConfig;
import com.wadpam.open.web.ProfileController;
import net.sf.mardao.core.dao.Dao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author sosandstrom
 */
@Configuration
public class ProfileConfig {

    @Bean
    public ProfileController openUserController() {
        return new ProfileController();
    }

    @Bean
    public ProfileService openUserService() {
        return new ProfileService();
    }

    @Bean
    public DProfileDaoBean dOpenUserDao() {
        return new DProfileDaoBean();
    }

}
