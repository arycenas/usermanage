package com.training.usermanage.service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@SuppressWarnings("deprecation")
@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    public String generateToken(UserDetails userDetails) {
        log.info("Generating token for user: {}", userDetails.getUsername());

        log.info("Token generated successfully for user: {}", userDetails.getUsername());
        return Jwts.builder().setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        log.info("Generating refresh token for user: {}", userDetails.getUsername());

        log.info("Refresh token generated successfully for user: {}", userDetails.getUsername());
        return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 432000000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        log.info("Extracting username from token");
        String username = extractClaims(token, Claims::getSubject);
        log.info("Username extracted: {}", username);
        return username;
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        log.info("Validating token for user: {}", userDetails.getUsername());

        final String username = extractUsername(token);
        if (isTokenExpired(token)) {
            log.error("Token is expired for user {}", userDetails.getUsername());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token has expired");
        }

        boolean isValid = (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        if (isValid) {
            log.info("Token is valid for user: {}", userDetails.getUsername());
        } else {
            log.error("Token is invalid for user: {}", userDetails.getUsername());
        }

        return isValid;
    }

    private Key getSigningKey() {
        log.debug("Getting signing key for token generation");

        byte[] key = Decoders.BASE64
                .decode("413F4428472B4B6250655368566D5970337336763979244226452948404D6351");

        return Keys.hmacShaKeyFor(key);
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        log.debug("Extracting claims from token");

        final Claims claims = extractAllClaims(token);

        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        log.debug("Extracting all claims from token");

        return Jwts.parser().setSigningKey(getSigningKey())
                .build().parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        log.info("Checking if token is expired");

        boolean isExpired = extractClaims(token, Claims::getExpiration)
                .before(new Date());

        if (isExpired) {
            log.error("Token is expired");
        } else {
            log.info("Token is not expired");
        }

        return isExpired;
    }
}
