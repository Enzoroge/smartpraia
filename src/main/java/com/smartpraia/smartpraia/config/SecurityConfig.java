package com.smartpraia.smartpraia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.Customizer;
import org.springframework.http.HttpMethod;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    } 

    private JwtAuthenticationConverter jwtAuthenticationConverter() {

    JwtAuthenticationConverter converter =
            new JwtAuthenticationConverter();

    converter.setJwtGrantedAuthoritiesConverter(jwt -> {

        String perfil = jwt.getClaimAsString("perfil");

        return List.of(
                new SimpleGrantedAuthority("ROLE_" + perfil)
        );
    });

    return converter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
       throws Exception {

        http
            .csrf(AbstractHttpConfigurer::disable)

            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            )

            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/login").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers(
                    HttpMethod.POST,
                  
                    "/usuario"
                ).hasAnyRole("PROPRIETARIO", "GERENTE")
                
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)

            .oauth2ResourceServer(oauth2 -> oauth2
               .jwt(jwt -> jwt
                .jwtAuthenticationConverter(jwtAuthenticationConverter())
               )
            );


        return http.build();
        
    }

}
