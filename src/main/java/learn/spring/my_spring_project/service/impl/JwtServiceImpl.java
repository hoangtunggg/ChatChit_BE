package learn.spring.my_spring_project.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import learn.spring.my_spring_project.entity.User;
import learn.spring.my_spring_project.service.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtServiceImpl implements JwtService {
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Override
    public String extractUsername(String token) {
        try {
            return extractAllClaims(token).getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String generateAccessToken(User user) {
        return buildToken(user.getUsername(), 1000 * 60 * 15, "access"); // 15 phu    t
    }

    @Override
    public String generateRefreshToken(User user) {
        return buildToken(user.getUsername(), 1000L * 60 * 60 * 24 * 7, "refresh"); // 7 ngay
    }


    private boolean isTokenValid(String token, User user){
        String username = extractUsername(token);
        return (username != null && username.equals(user.getUsername())) && !isTokenExpired(token);
    }

    public boolean isAccessTokenValid(String token, User user) {
        return isTokenValid(token, user);
    }

    public boolean isRefreshTokenValid(String token, User user) {
        return isTokenValid(token, user);
    }


    private String buildToken (String subject, long expirationMillis, String type){
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .claim("type", type)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private boolean isTokenExpired(String token){
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
