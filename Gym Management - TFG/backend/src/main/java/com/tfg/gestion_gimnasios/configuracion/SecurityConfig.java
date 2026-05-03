package com.tfg.gestion_gimnasios.configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JWTRequestFilter jwtRequestFilter) throws Exception {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowCredentials(true);
        corsConfig.addAllowedOriginPattern("*");
        corsConfig.addAllowedHeader("*");
        corsConfig.addExposedHeader("*");
        corsConfig.addAllowedMethod("*");
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        CorsFilter corsFilter = new CorsFilter(source);

        http
            .csrf().disable()
            .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/gestiongimnasios/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/gestiongimnasios/").permitAll()

                
                .requestMatchers(HttpMethod.GET, "/gestiongimnasios/gimnasios/*/usuarios").hasAnyRole("ADMIN", "ENTRENADOR", "USUARIO")
                .requestMatchers(HttpMethod.POST, "/gestiongimnasios/entrenadores/**").hasAnyRole("ADMIN", "ENTRENADOR")
                .requestMatchers(HttpMethod.GET, "/gestiongimnasios/usuarios/{email}/rutinas")
                .access(new WebExpressionAuthorizationManager(
                    "#email == authentication.name or hasAnyRole('ADMIN','ENTRENADOR')"
                ))

                .anyRequest().permitAll()
            )
            .formLogin().disable();

        http.addFilterAfter(jwtRequestFilter, CorsFilter.class);

        return http.build();
    }
}
