package com.mycompany.myproject.initializer;

import com.mycompany.myproject.config.MvcConfig;
import com.mycompany.myproject.config.PersistConfig;
import com.mycompany.myproject.config.SecurityConfig;
import com.mycompany.myproject.config.SocialConfig;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class MvcWebApplicationInitializer  extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{SecurityConfig.class, SocialConfig.class, PersistConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[] {MvcConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

}
