//package com.ll.hereispaw.global.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.cors.CorsConfiguration;
//
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//
//@Configuration
//public class CorsConfig {
//
//    @Value("${services.gateway.url}")
//    private String apiGatewayUrl;
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//
//        // API Gateway의 호스트만 허용
//        configuration.addAllowedOrigin("http://localhost:5173"); // 클라이언트 URL
//        configuration.addAllowedOrigin(apiGatewayUrl);
//
//        configuration.addAllowedMethod("");
//        configuration.addAllowedHeader("");
//        configuration.setAllowCredentials(false);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
//}