package org.mifos.pheebillpay.interceptor.config;

import org.mifos.pheebillpay.interceptor.HeaderValidationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final HeaderValidationInterceptor headerValidationInterceptor;

    @Autowired
    public WebMvcConfig(HeaderValidationInterceptor headerValidationInterceptor) {
        this.headerValidationInterceptor = headerValidationInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(headerValidationInterceptor);
    }
}
