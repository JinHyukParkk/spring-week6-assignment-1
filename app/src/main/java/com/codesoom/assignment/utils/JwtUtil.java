package com.codesoom.assignment.utils;

import com.codesoom.assignment.errors.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtUtil {

    private final Key key;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * userId로 인코딩된 jwt를 리턴합니다.
     *
     * @param userId Payload에 들어갈 userId
     * @return jwt
     */
    public String encode(Long userId) {

        return Jwts.builder()
                .claim("userId", userId)
                .signWith(key).compact();
    }

    /**
     * token을 디코딩하여 payload 정보를 리턴합니다.
     * @param token
     * @return
     */
    public Claims decode(String token) {
        if (token == null || token.isBlank()) {
            throw new InvalidTokenException(token);
        }

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SignatureException e) {
            throw new InvalidTokenException(token);
        }
    }
}
