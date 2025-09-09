package com.passtival.backend.global.security.util;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
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
			// 필수 정보가 누락된 경우 - 잘못된 토큰 형태
			throw new JwtException("잘못된 토큰 형태");

		} catch (ExpiredJwtException e) {
			// 토큰 만료 ExpiredJwtException그대로 던지기
			throw e;
		} catch (JwtException e) {
			// 이미 JwtException인 경우 그대로 던짐
			throw e;
		} catch (Exception e) {
			// 그 외 예상치 못한 예외들을 JwtException으로 래핑
			throw new JwtException("JWT 토큰 파싱 실패", e);
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
	}
}