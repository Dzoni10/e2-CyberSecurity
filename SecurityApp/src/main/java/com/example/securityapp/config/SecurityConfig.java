package com.example.securityapp.config;

import com.example.securityapp.auth.JwtAuthenticationFilter;
import com.example.securityapp.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {}) // koristi globalni WebMvcConfigurer ako ga imaš
                .authorizeHttpRequests(auth -> auth
                        // Swagger potpuno otvoren
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        // Public endpoints
                        .requestMatchers(HttpMethod.POST, "/api/users/signup").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/verify").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/recovery").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/registerCA").hasRole("ADMIN")

                        // Protected endpoints (role-based)
                        .requestMatchers(HttpMethod.GET, "/api/certificates/all").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/certificates/ca").hasRole("BASIC")
                        .requestMatchers(HttpMethod.POST, "/api/certificates/issue").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/csr/upload").hasRole("BASIC")
                        .requestMatchers(HttpMethod.GET, "/api/users/sessions").hasRole("BASIC")

                       // .requestMatchers(HttpMethod.POST, "/api/password-manager/entries").hasRole("BASIC")
                        //.requestMatchers(HttpMethod.GET, "/api/password-manager/entries").hasRole("BASIC")
                        //.requestMatchers(HttpMethod.POST, "/api/password-manager/share").hasRole("BASIC")
                        //.requestMatchers(HttpMethod.DELETE, "/api/password-manager/entries/**").hasRole("BASIC")
                        //.requestMatchers(HttpMethod.GET, "/api/password-manager/public-key").hasRole("BASIC")
                        //.requestMatchers(HttpMethod.GET, "/api/password-manager/public-key/**").hasRole("BASIC")

                        .requestMatchers(HttpMethod.GET, "/api/password-manager/**").hasRole("BASIC")
                        .requestMatchers(HttpMethod.POST, "/api/password-manager/**").hasRole("BASIC")
                        .requestMatchers(HttpMethod.DELETE, "/api/password-manager/**").hasRole("BASIC")

                        // Sve ostalo traži auth
                        .anyRequest().authenticated()
                )
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(httpBasic -> {})
                //.formLogin(form -> {})
                //.logout(logout -> logout.disable())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authProvider(CustomUserDetailsService userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}
