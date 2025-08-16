package com.passtival.backend.global.auth.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtUtil {

	private SecretKey secretKey;

	@Value("${jwt.access-token-expiration}")
	private long accessTokenExpiration;

	@Value("${jwt.refresh-token-expiration}")
	private long refreshTokenExpiration;

	public JwtUtil(@Value("${jwt.secret}") String secret) {
		this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
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