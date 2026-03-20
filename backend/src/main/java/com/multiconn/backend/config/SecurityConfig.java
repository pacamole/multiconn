package com.multiconn.backend.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.multiconn.backend.service.CustomOAuth2UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
        private static final String[] publicRoutes = {
                        "/",
                        "/public/**",
                        "/api/payments/webhook"
        };

        private final CustomOAuth2UserService authService;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

                // CROSS-ORIGIN RESOURCE SHARING
                http.cors(Customizer.withDefaults());

                // CROSS-SITE REQUEST FORGERY
                http.csrf(csrf -> csrf.disable());

                // REQUEST AUTHORIZATION CONFIG
                http.authorizeHttpRequests(auth -> auth
                                .requestMatchers(publicRoutes).permitAll()
                                .anyRequest().authenticated());

                // GOOGLE LOGIN CONFIG
                http.oauth2Login(oauth2 -> oauth2
                                .userInfoEndpoint(userInfo -> userInfo.userService(authService))
                                .defaultSuccessUrl("http://localhost:4200/chat", true));
                return http.build();
        }

        @Bean
        CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();

                List<String> allowedOrigins = List.of(
                                "http://localhost:4200", "http://localhost:8080");

                List<String> allowedMethods = List.of(
                                "GET", "POST", "PUT", "DELETE", "OPTIONS");
                List<String> allowedHeaders = List.of(
                                "Authorization", "Content-Type", "X-Requested-With", "Accept");

                configuration.setAllowedOriginPatterns(allowedOrigins);
                configuration.setAllowedMethods(allowedMethods);
                configuration.setAllowedHeaders(allowedHeaders);

                // Allows Angular to send JSESSIONID (cookie) in the requests
                configuration.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration); // Applies CORS to every endpoint
                return source;
        }

}
