package com.example.demo.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {
    private static final String SECRET =
            "replace-with-a-very-long-random-secret-string-which-is-at-least-256-bits-long-1234567890";
    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());
    private final long expirationMillis = 2 * 60 * 60 * 1000; // 2小时

    public String generateToken(UserDetails user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(user.getUsername()) // 我们把 email 当作 subject
                .setIssuedAt(Date.from(now))
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }
}
