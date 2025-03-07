package com.ll.hereispaw.global.config;

import com.ll.hereispaw.global.security.CustomAuthenticationFilter;
import com.ll.hereispaw.global.security.CustomAuthorizationRequestResolver;
import com.ll.hereispaw.global.security.CustomOAuth2AuthenticationSuccessHandler;
import com.ll.hereispaw.global.security.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomAuthenticationFilter customAuthenticationFilter;
    private final CustomOAuth2AuthenticationSuccessHandler customOAuth2AuthenticationSuccessHandler;
    private final CustomAuthorizationRequestResolver customAuthorizationRequestResolver;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:5173",
            "https://www.pawpaw.kknaks.site",
            "https://pawpaw.kknaks.site"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain baseSecurityFilterChain(HttpSecurity http, CustomOAuth2UserService customOAuth2UserService) throws Exception {

        http
//                .securityMatcher("")
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/h2-console/**").permitAll()
                                .requestMatchers("/swagger-ui/index.html").permitAll()
                                //.requestMatchers("/api/v1/chat/**").permitAll()
                                .requestMatchers("/api/v1/members/me").permitAll()
                                .requestMatchers("/api/v1/members/signup").permitAll()
                                .requestMatchers("/api/v1/members/login").permitAll()
                                .requestMatchers("/api/v1/members/logout").permitAll()
//                                .requestMatchers("/api/v1/missings").permitAll()
                                .requestMatchers("/api/*/**")
                                .authenticated()
                                .anyRequest()
                                .permitAll()
                )
                .headers(
                        headers ->
                                headers.frameOptions(
                                        HeadersConfigurer.FrameOptionsConfig::sameOrigin
                                )
                )
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .formLogin(
                        AbstractHttpConfigurer::disable
                )
                .sessionManagement((sessionManagement) -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .oauth2Login(
                        oauth2Login -> oauth2Login
                                .successHandler(customOAuth2AuthenticationSuccessHandler)
                                .authorizationEndpoint(
                                        authorizationEndpoint -> authorizationEndpoint
                                                .authorizationRequestResolver(customAuthorizationRequestResolver)
                                )
                )
                .addFilterBefore(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
