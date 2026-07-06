package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射 /uploads/** 到本地目录
        registry.addResourceHandler("/avatars/**")
                .addResourceLocations("file:" + uploadPath + "/");
       registry.addResourceHandler("/template-images/**").addResourceLocations("file:D:/JL/demo/images/JL/");
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:D:/JL/demo/images/");
    }
}
