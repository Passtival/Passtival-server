package com.passtival.backend.global.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class JWTUtil {

    private SecretKey secretKey;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public JWTUtil(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // memberId 추출
    public Long getMemberIdToken(String token) {
        try{
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("memberId", Long.class);
        }catch (Exception e){
            //예외처리
            return null;
        }
    }

    // role 추출
    public String getRole(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("role", String.class);
        }catch (Exception e){
            return null;
        }
    }

    // 만료 시간 계산
    public Long getExpiration(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Date expiration = claims.getExpiration();
            return expiration.getTime() - System.currentTimeMillis();
        } catch (Exception e) {
            return -1L;
        }
    }

    // 토큰에서 사용자 정보 Claims 추출
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new RuntimeException("토큰 파싱 실패", e);
        }
    }

    // 토큰 만료 여부 확인
    public Boolean isExpiredToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    // 토큰 유효성 검증
    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return !isExpiredToken(token);
        } catch (Exception e) {
            log.warn("토큰 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    public TokenValidationResult validateAndExtractToken(String token) {
        try {
            // 1. 토큰을 단 한 번만 파싱합니다.
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // 2. 파싱된 claims 객체에서 모든 정보를 추출합니다.
            Long memberId = claims.get("memberId", Long.class);
            String role = claims.get("role", String.class);
            Date expiration = claims.getExpiration();

            // 필수 정보 존재 여부를 확인합니다.
            // 파싱이 성공하고 필수 정보가 있다면, 토큰은 유효합니다.
            boolean isValid = (memberId != null && role != null);

            return new TokenValidationResult(memberId, role, expiration, isValid);

        } catch (Exception e) {
            // ExpiredJwtException, SignatureException 등 모든 예외 발생 시 유효하지 않은 토큰으로 간주합니다.
            log.warn("토큰 검증 및 정보 추출 실패: {}", e.getMessage());
            return TokenValidationResult.invalid();
        }
    }
    public TokenInfo extractTokenInfo(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            // 2. 파싱된 claims 객체에서 정보를 추출합니다.
            Long memberId = claims.get("memberId", Long.class);
            String role = claims.get("role", String.class);
            Date expiration = claims.getExpiration();

            if (memberId != null && role != null) {
                return new TokenInfo(memberId, role, expiration);
            }
            // 필수 정보가 누락된 경우
            return null;

        } catch (Exception e) {
            // 파싱 과정에서 예외가 발생하면(만료, 오류 등) null을 반환합니다.
            return null;
        }
    }

    // Access Token 생성
    public String createAccessToken(Long memberId, String role) {
        return Jwts.builder()
                .claim("memberId", memberId)
                .claim("role", role)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // Refresh Token 생성
    public String createRefreshToken(Long memberId, String role) {
        return Jwts.builder()
                .claim("memberId", memberId)
                .claim("role", role)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
    public static class TokenValidationResult {
        public final Long memberId;
        public final String role;
        public final Date expiration;
        public final boolean isValid;

        public TokenValidationResult(Long memberId, String role, Date expiration, boolean isValid) {
            this.memberId = memberId;
            this.role = role;
            this.expiration = expiration;
            this.isValid = isValid;
        }

        public static TokenValidationResult invalid() {
            return new TokenValidationResult(null, null, null, false);
        }
    }

    // AuthService용
    public static class TokenInfo {
        public final Long memberId;
        public final String role;
        public final Date expiration;

        public TokenInfo(Long memberId, String role, Date expiration) {
            this.memberId = memberId;
            this.role = role;
            this.expiration = expiration;
        }

        public boolean isExpired() {
            return expiration.before(new Date());
        }
    }
}
