package com.gateway.filter;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;
import org.springframework.web.server.ServerWebExchange;

import com.gateway.util.JwtUtil;

import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter,Ordered {
	
	@Autowired
	private JwtUtil jwtUtil;

	@Override
	public int getOrder() {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		// TODO Auto-generated method stub
		String path = exchange.getRequest().getURI().getPath();
		
		//Allow auth endpoints without token
		if(path.startsWith("/auth") || path.startsWith("/register")) {
			return chain.filter(exchange);
		}
		
		String authHeader = exchange.getRequest().getHeaders().getFirst(
				HttpHeaders.AUTHORIZATION);
		
		if(authHeader == null || !authHeader.startsWith("Bearer ")) {
			return unauthorized(exchange);
		}
		
		String token = authHeader.substring(7);
		
		try {
			Claims claims = jwtUtil.validateToken(token);
			String email = claims.getSubject();
			
			ServerHttpRequest mutatedRequest = exchange.getRequest()
					                              .mutate()
					                              .header("X-User-Email",email)
					                              .build();
			
			return chain.filter(exchange.mutate().request(mutatedRequest).build());
			
		}catch(Exception e) {
			return unauthorized(exchange);
		}
	}

	private Mono<Void> unauthorized(ServerWebExchange exchange) {
		// TODO Auto-generated method stub
		
		exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
		
		return exchange.getResponse().setComplete();
	}

}
