package com.example.securityapp.auth;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private SessionRegistry sessionRegistry;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // PRVA PROVERA: Da li je ovo Swagger putanja?
        String path = request.getRequestURI();
        if (path.startsWith("/swagger-ui") ||
                path.equals("/swagger-ui.html") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-resources") ||
                path.startsWith("/webjars")) {

            System.out.println("SWAGGER PATH - SKIPPING JWT COMPLETELY: " + path);
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // izbacivanje Bearer

            try{
                Claims claims = JwtUtil.parseJWT(token);

                Number userIdNumber = claims.get("userId", Number.class);
                int userId = userIdNumber.intValue();
                String sessionId = claims.get("sessionId", String.class);
                String role = claims.get("role", String.class);

                if(sessionId == null || role == null) {
                    throw new RuntimeException("Invalid token claims");
                }

                List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

                sessionRegistry.updateLastActivity(sessionId);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, authorities);

                // Dodaj detalje zahteva
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);

                System.out.println("JWT Authentication successful for user: " + userId + ", role: " + role);

            }catch (Exception e) {
                System.out.println("JWT Authentication failed: " + e.getMessage());
                SecurityContextHolder.clearContext();
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Invalid or expired token");
                return;
            }
        } else {
            // Nema Authorization header - pusti Spring Security da odluči
            System.out.println("No Authorization header for path: " + path);
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();

        // Ukloni context path ako postoji
        if (contextPath != null && !contextPath.isEmpty()) {
            path = path.substring(contextPath.length());
        }

        boolean shouldSkip = path.startsWith("/swagger-ui") ||
                path.equals("/swagger-ui.html") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-resources") ||
                path.startsWith("/webjars");

        System.out.println("=== JWT FILTER DEBUG ===");
        System.out.println("Original URI: " + request.getRequestURI());
        System.out.println("Context Path: " + contextPath);
        System.out.println("Processed Path: " + path);
        System.out.println("Should Skip Filter: " + shouldSkip);
        System.out.println("=========================");

        return shouldSkip;
    }
}