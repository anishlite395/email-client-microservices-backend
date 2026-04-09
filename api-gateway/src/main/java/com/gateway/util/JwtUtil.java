package com.gateway.util;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class JwtUtil {

	private final String SECRET = "5367566859703373367639792F423F452848284D6251655468576D5A71347437";
	
	public Claims validateToken(String token) {
		return Jwts.parser()
				.setSigningKey(SECRET)
				.parseClaimsJws(token)
				.getBody();
	}
}
