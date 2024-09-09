package com.myrecipes.backend.config;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")  // フロントエンドのURLを指定
                .allowedMethods("GET", "POST", "PUT", "DELETE")  // 許可するHTTPメソッド
                .allowedHeaders("*");  // 許可するヘッダー
    }
}
