package com.chubb.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String uri = request.getRequestURI();

        // Skip public endpoints
        if (uri.contains("/auth/login") || uri.contains("/auth/register")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String jwt = authHeader.substring(7);

            try {
                Claims claims = jwtUtil.extractAllClaims(jwt);

                String email = claims.getSubject();
                String role = claims.get("role", String.class);

                Boolean forceChange =
                        claims.get("forcePasswordChange", Boolean.class);

                if (email != null &&
                    role != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                    // Force password change check
                    if (Boolean.TRUE.equals(forceChange)
                    	    && !"ADMIN".equalsIgnoreCase(role)
                    	    && !uri.startsWith("/api/auth")) {

                        response.sendError(
                                HttpServletResponse.SC_FORBIDDEN,
                                "Password expired. Change password required."
                        );
                        return;
                    }

                    List<SimpleGrantedAuthority> authorities =
                            List.of(new SimpleGrantedAuthority("ROLE_" + role));

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    email,
                                    null,
                                    authorities
                            );

                    auth.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder.getContext()
                            .setAuthentication(auth);
                }

            } catch (Exception e) {
                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Invalid or expired JWT"
                );
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
