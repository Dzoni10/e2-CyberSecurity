package com.example.securityapp.config;

import com.example.securityapp.auth.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.example.securityapp.service.CustomUserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    JwtAuthenticationFilter jwtAuthenticationFilter;


    //@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") //resava problem http objekta
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf->csrf.disable())
                .cors(cors -> {}) //DA BI KORISTIO CORS IZ GLAVNE KLASE WEB MVC CONFIGURER
                .authorizeHttpRequests(auth-> auth
                        .requestMatchers(HttpMethod.POST, "/api/users/signup").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/verify").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/recovery").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/sessions").hasRole("BASIC")
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic->{})
                .formLogin(form->form.disable())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // ⬅ dodato

        return http.build();
    }


   //@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    public DaoAuthenticationProvider authenticationProvider(CustomUserDetailsService userDetailsService,
                                                            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

//    @Bean
//    public AuthenticationManagerBuilder authenticationManagerBuilder(
//            AuthenticationManagerBuilder auth,
//            DaoAuthenticationProvider authenticationProvider) {
//        auth.authenticationProvider(authenticationProvider);
//        return auth;
//    }

}
