package org.ediae.tfm.crmapi.config;

import org.ediae.tfm.crmapi.security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
          // 1) Aplicamos aquí tu CORS real (no un cors vacío)
          .cors(cors -> cors.configurationSource(corsConfigurationSource()))
          // 2) Desactivamos CSRF para REST
          .csrf(csrf -> csrf.disable())
          // 3) Stateless: no guardamos sesión en el servidor
          .sessionManagement(sm -> 
              sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
          )
          // 4) Reglas de autorización
          .authorizeHttpRequests(auth -> auth
              // a) Permitimos preflight y POST a /appUser/registro
              .requestMatchers(HttpMethod.OPTIONS, "/appUser/registro").permitAll()
              .requestMatchers(HttpMethod.GET,   "/appUser/registro").permitAll()
              .requestMatchers(HttpMethod.POST,   "/appUser/registro").permitAll()
              // b) También login y Swagger UI
              .requestMatchers(
                  "/appUser/login",
                  "/v3/api-docs/**",
                  "/swagger-ui/**",
                  "/swagger-ui.html"
              ).permitAll()
              .requestMatchers("/error").permitAll()
              // c) El resto solo con JWT válido
              .anyRequest().authenticated()
          ) // <- Cierra el bloque de authorizeHttpRequests
          // 5) Filtro JWT desactivado temporalmente para depuración
          .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Define aquí tu política CORS para que Spring Security la aplique:
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
            "http://localhost:4200",
            "https://crm-frontend-production-5629.up.railway.app",
            "https://crm-backend-production-5a64.up.railway.app"
        ));
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplica esta política a todas las rutas
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        return http
//                .cors(cors -> {}) // Enables CORS using your WebMvcConfigurer
//                .csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests(auth -> auth
//                        .anyRequest().permitAll() // Allow all requests
//                )
//                // TEMPORARILY DISABLED: JWT filter
//                // .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
//                .build(); // ✅ Don't forget to build the chain
//    }