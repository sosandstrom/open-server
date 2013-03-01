/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.spring;

import com.google.appengine.api.utils.SystemProperty;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * Disabling XML Validation in Production
 * @author sosandstrom
 */
public class CustomXmlWebApplicationContext extends XmlWebApplicationContext {

    @Override
    protected void initBeanDefinitionReader(XmlBeanDefinitionReader beanDefinitionReader) {
        super.initBeanDefinitionReader(beanDefinitionReader);
        if (SystemProperty.Environment.Value.Production == SystemProperty.environment.value()) {
            beanDefinitionReader.setValidationMode(XmlBeanDefinitionReader.VALIDATION_NONE);
        }
    }

}
