package com.example.securityapp.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.net.URI;

@Component
public class SwggerOpener {

    @Value("${server.port:8080}")
    private int port;

    @PostConstruct
    public void openSwaggerUI() {
        try {
            String url = "http://localhost:" + port + "/swagger-ui/index.html";
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception e) {
            System.out.println("Could not open Swagger UI automatically: " + e.getMessage());
        }
    }
}
