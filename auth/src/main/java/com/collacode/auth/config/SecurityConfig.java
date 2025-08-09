package com.collacode.auth.config;

import com.collacode.auth.component.JwtConverter;
import com.collacode.auth.service.CustomOidcUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Autowired
    private JwtConverter jwtConverter;
    @Autowired
    private CustomOidcUserService oidcUserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable) // Отключить CSRF для API
                .authorizeHttpRequests(request -> {
                    request
                            .requestMatchers("/collacode/v1/auth/token", "/collacode/v1/auth/register", "/collacode/v1/auth/validate","/v3/api-docs/**",
                                    "/swagger-ui/**",
                                    "/swagger-resources/**",
                                    "/webjars/**").permitAll()
                            .anyRequest().authenticated();
                })
                .oauth2Login(oauth2 ->
                        oauth2.userInfoEndpoint(userInfo ->
                                userInfo.oidcUserService(oidcUserService)
                        ))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(
                        jwt -> jwt.jwtAuthenticationConverter(jwtConverter)
                ))
                .build();
    }
}
