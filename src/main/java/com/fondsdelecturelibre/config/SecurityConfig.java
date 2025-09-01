package com.fondsdelecturelibre.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtRequestFilter jwtRequestFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> {
                auth.requestMatchers(
                    "/api/auth/**", 
                    "/api/test/public",
                    "/v3/api-docs/**", 
                    "/swagger-ui/**", 
                    "/swagger-ui.html",
                    "/swagger-resources/**",
                    "/webjars/**",
                    "/actuator/health"
                ).permitAll();
                
                auth.requestMatchers(HttpMethod.GET, "/api/ebooks").hasAnyRole("MEMBER", "ADMIN");
                auth.requestMatchers(HttpMethod.GET, "/api/ebooks/**").hasAnyRole("MEMBER", "ADMIN");
                auth.requestMatchers(HttpMethod.GET, "/api/ebooks/download/**").hasAnyRole("MEMBER", "ADMIN");
                auth.requestMatchers(HttpMethod.GET, "/api/ebooks/search/**").hasAnyRole("MEMBER", "ADMIN");
                auth.requestMatchers(HttpMethod.POST, "/api/ebooks").hasAnyRole("MEMBER", "ADMIN");
                auth.requestMatchers(HttpMethod.PUT, "/api/ebooks/**").hasAnyRole("MEMBER", "ADMIN");
                auth.requestMatchers(HttpMethod.DELETE, "/api/ebooks/**").hasAnyRole("MEMBER", "ADMIN");
                
                auth.requestMatchers("/api/ebooks/*/reviews").hasAnyRole("MEMBER", "ADMIN");
                auth.requestMatchers("/api/ebooks/*/reviews/**").hasAnyRole("MEMBER", "ADMIN");
                
                auth.requestMatchers(HttpMethod.GET, "/api/categories").hasAnyRole("MEMBER", "ADMIN");
                auth.requestMatchers(HttpMethod.GET, "/api/categories/**").hasAnyRole("MEMBER", "ADMIN");
                auth.requestMatchers(HttpMethod.POST, "/api/categories").hasRole("ADMIN");
                auth.requestMatchers(HttpMethod.PUT, "/api/categories/**").hasRole("ADMIN");
                auth.requestMatchers(HttpMethod.DELETE, "/api/categories/**").hasRole("ADMIN");
                
                auth.requestMatchers("/api/dashboard").hasAnyRole("MEMBER", "ADMIN");
                
                auth.requestMatchers("/api/users/**").hasRole("ADMIN");
                
                auth.requestMatchers("/api/**").authenticated();
                
                auth.anyRequest().denyAll();
            })
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(false); // Changed to false for broader compatibility
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Disposition", "Content-Type", "Content-Length"));
        configuration.setMaxAge(3600L); // Cache preflight for 1 hour
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
