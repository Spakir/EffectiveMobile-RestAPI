package org.example.effectivemobilerestapi.services.interfaces;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

public interface JwtService {

    String extractUsername(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsReceiver);

    String generateToken(UserDetails user);

    boolean isTokenValid(String token, String username);

    String buildToken(Map<String, Object> claims, String username, long expireTime);

    boolean isTokenExpired(String token);

    Date extractExpiration(String token);

    Claims extractAllClaims(String token);

    Key getKey();
}
