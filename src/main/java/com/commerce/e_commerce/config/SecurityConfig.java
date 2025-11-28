package com.commerce.e_commerce.config;

import com.commerce.e_commerce.security.JwtAuthenticationFilter;
import com.commerce.e_commerce.security.JwtTokenProvider;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var cfg = new org.springframework.web.cors.CorsConfiguration();
        cfg.addAllowedHeader("*");
        // JWT'yi header'da taşıyorsan credentials'a gerek yok:
        cfg.setAllowCredentials(false); // cookie kullanacaksan true yap
        cfg.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:3000"
                // prod domainlerin: "https://shop.yourdomain.com" vb.
        ));
        cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS","PATCH"));
        // cfg.setAllowedHeaders(List.of("Authorization","Content-Type","Accept","X-Requested-With"));
        // İstersen response'ta göstermek istediklerin:
        cfg.setExposedHeaders(List.of("Authorization"));
        cfg.setMaxAge(3600L); // saniye

        var source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        var jwtFilter = new JwtAuthenticationFilter(jwtTokenProvider);

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // CORS preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Swagger
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // Auth uçları açık
                        .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login", "/api/auth/refresh").permitAll()

                        // Vitrin GET'leri (anonim erişim)
                        .requestMatchers(HttpMethod.GET, "/api/catalog/**", "/api/content/**", "/api/marketing/**").permitAll()

                        //Sepet tüm uçlar açık
                        .requestMatchers("/api/cart/**").authenticated()

                        // --- YÖNETİM/YAZMA OPERASYONLARI SADECE ADMIN ---
                        .requestMatchers(HttpMethod.POST,   "/api/catalog/**", "/api/content/**", "/api/marketing/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/catalog/**", "/api/content/**", "/api/marketing/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH,  "/api/catalog/**", "/api/content/**", "/api/marketing/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/catalog/**", "/api/content/**", "/api/marketing/**").hasRole("ADMIN")

                        // (Varsa) yönetim paneli
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // geri kalan her şey auth ister
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> res.sendError(401))
                        .accessDeniedHandler((req, res, e) -> res.sendError(403))
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // DaoAuthenticationProvider mutlaka passwordEncoder'ı da bilmeli
    @Bean
    public AuthenticationManager authManager(UserDetailsService userDetailsService, PasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(encoder);
        return new ProviderManager(provider);
    }

    // Swagger UI'da "Authorize" (Bearer) butonu
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes("Bearer Authentication",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                ));
    }
}
