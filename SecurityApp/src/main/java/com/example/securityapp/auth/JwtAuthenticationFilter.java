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
                        new UsernamePasswordAuthenticationToken(claims.get("userId").toString(), null,
                                authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);//ubacivanje autentifikovanog korisnika u context holder
            }catch (Exception e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Invalid or expired token");
                return;
            }
        }
        filterChain.doFilter(request,response);
    }
}
