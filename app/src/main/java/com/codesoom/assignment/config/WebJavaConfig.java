package com.codesoom.assignment.config;

import com.codesoom.assignment.interceptors.AuthenticationInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebJavaConfig implements WebMvcConfigurer {

    private AuthenticationInterceptor authenticationInterceptor;

    public WebJavaConfig(AuthenticationInterceptor authenticationInterceptor) {
        this.authenticationInterceptor = authenticationInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor);
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
