package com.example.securityapp.auth;

import javax.crypto.SecretKey;

import com.example.securityapp.domain.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET = "secret_password_for_encoding_messages";
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    public static String generateToken(int userId, Role role,String sessionId) {

        long nowMillis = System.currentTimeMillis();
        long expMillis = nowMillis + 24*60*60*1000;  //24h traje token

        return Jwts.builder()
                .claim("userId",userId)
                .claim("role",role.name())
                .claim("sessionId", sessionId)
                .setIssuedAt(new Date(nowMillis))
                .setExpiration(new Date(expMillis))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static Claims parseJWT(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
