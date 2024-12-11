package com.example.ticketing_system_spring_boot.websocket_config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Ensure this doesn't conflict with your WebSocket endpoint
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }
}

