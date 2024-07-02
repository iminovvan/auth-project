package com.lorby.auth_project.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.lorby.auth_project.entity.User;
import com.lorby.auth_project.entity.enums.TokenType;
import com.lorby.auth_project.exception.TokenValidationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
@Slf4j
public class JwtService {
    private static final String SECRET_KEY = System.getenv("SECRET_KEY");
    private static final String TOKEN_USERNAME = "username";

    public String generateToken(User user, long expirationMinutes) {
        Map<String, Object> claims = new HashMap<>();
        List<String> roleList = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        claims.put("roles", roleList);
        claims.put(TOKEN_USERNAME, user.getUsername());

        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        Key key = new SecretKeySpec(keyBytes, 0, keyBytes.length, "HmacSHA256");

        long expirationMillis = expirationMinutes * 60 * 1000L;

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(key)
                .compact();
    }

    public Boolean validateToken(String token, User user) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
            Algorithm algorithm = Algorithm.HMAC256(keyBytes);
            JWTVerifier jwtVerifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = jwtVerifier.verify(token);
            String username = decodedJWT.getClaim(TOKEN_USERNAME).asString();
            boolean isExpired = isTokenExpired(decodedJWT);
            boolean isValid = username != null && username.equals(user.getUsername()) && !isExpired;
            return isValid;
        } catch (JWTVerificationException ex){
            throw new TokenValidationException("Token validation failed: {}" + ex.getMessage());
        } catch (Exception ex){
            throw new TokenValidationException("Token validation failed: {}" + ex.getMessage());
        }
    }

    public Boolean isTokenExpired(DecodedJWT decodedJWT) {
        return decodedJWT.getExpiresAt().before(new Date());
    }

    private Claims getAllClaimsFromToken(String token){
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        Key key = new SecretKeySpec(keyBytes, 0, keyBytes.length, "HmacSHA256");
        return Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public List<String> getRoles(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            Object roles = claims.get("roles");
            if (roles instanceof List<?>) {
                return ((List<?>) roles).stream()
                        .filter(obj -> obj instanceof String)
                        .map(obj -> (String) obj)
                        .collect(Collectors.toList());
            }
            return Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractUsername(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    public Long extractUserId(String token) {
        return Long.parseLong(getAllClaimsFromToken(token).getId());
    }
}
