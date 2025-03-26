package org.example.effectivemobilerestapi.service.impls;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.effectivemobilerestapi.service.interfaces.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${spring.security.jwt.secret-key}")
    private String secretKey;

    @Value("${spring.security.jwt.expiration-time}")
    private long expirationTime;

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsReceiver) {
        Claims claims = extractAllClaims(token);

        return claimsReceiver.apply(claims);
    }

    @Override
    public String generateToken(String username) {
        return generateToken(new HashMap<>(), username);
    }

    @Override
    public String generateToken(Map<String, Object> claims, String username) {
        return buildToken(claims, username, expirationTime);
    }

    @Override
    public boolean isTokenValid(String token, String username) {
        String tokenUsername = extractUsername(token);

        return (tokenUsername.equals(username)) && !isTokenExpired(token);
    }

    @Override
    public String buildToken(Map<String, Object> claims,String username, long expireTime) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(getKey())
                .compact();
    }

    @Override
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    @Override
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    @Override
    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }catch (Exception e){
            throw new JwtException("Невалидный Jwt токен: " + e.getMessage());
        }
    }

    @Override
    public Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
