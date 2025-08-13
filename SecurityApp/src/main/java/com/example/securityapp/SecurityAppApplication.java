package com.example.securityapp;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@SpringBootApplication
@EnableAsync
public class SecurityAppApplication implements WebMvcConfigurer {

    @Bean
    public ModelMapper getModelMapper()
    {
        return new ModelMapper();
    }

    public static void main(String[] args) {
        SpringApplication.run(SecurityAppApplication.class, args);
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//        System.out.println(encoder.matches("admin123", "$2a$10$Dow1E2F6wNwKj5oFzpFsaOeP4nK3pHz5fVQ4qP5B.YA5jE6cBQzvS"));
//
//        String hash = encoder.encode("basic123");
//        System.out.println(hash);
    }


    @Bean
    public WebMvcConfigurer corsConfigurer(){
        return new WebMvcConfigurer(){
            @Override
            public void addCorsMappings(CorsRegistry registry){
                registry.addMapping("/api/**")
                        .allowedOrigins("http://localhost:4200")
                        .allowedMethods("GET","POST","PUT","DELETE","OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
