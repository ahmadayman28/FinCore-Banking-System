package com.fincore.fincorebank.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;

@Service
public class TokenService {
    
    private SecretKey key;
    
    @Value("${jwt.secret.string}")
    private String JWT_SECRET;
    
    @Value("${jwt.expiration.time}")
    private Long EXPIRATION_TIME; 
    
    @PostConstruct
    private void init() {
        byte[] keyBytes = JWT_SECRET.getBytes(StandardCharsets.UTF_8);
        this.key = new SecretKeySpec(keyBytes, "HmacSHA256");
    }
    
    public String generateToken(String email) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date(now))
                .expiration(new Date(now + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }
    
    public String getUserNameFromToken(String token) {
    	return extractClaims(token, Claims::getSubject);
    }

	private <T> T extractClaims(String token, Function<Claims, T> claimsFunction) {
		return claimsFunction.apply(Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload());
	}
	
	public boolean isValidToken(String token, UserDetails userDetails) {
		final String userName= getUserNameFromToken(token);
		return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	private boolean isTokenExpired(String token) {
		return extractClaims(token, Claims::getExpiration).before(new Date());
	}
}