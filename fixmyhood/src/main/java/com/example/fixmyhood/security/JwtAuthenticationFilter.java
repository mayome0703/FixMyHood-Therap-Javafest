package com.example.fixmyhood.security;

import com.example.fixmyhood.service.implementation.AdminDetailsServiceImpl;
import com.example.fixmyhood.service.implementation.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;
    private final AdminDetailsServiceImpl adminDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        System.out.println("Request path: " + path); // Keep this for debugging


        if (path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/swagger-resources")
                || path.startsWith("/swagger-ui.html")
                || path.startsWith("/swagger-ui/index.html")
                || path.contains("swagger")
                || path.contains("api-docs")
                || path.startsWith("/webjars/")           // ← Add this
                || path.startsWith("/configuration/")     // ← Add this
                || path.startsWith("/api/auth")
                || path.startsWith("/api/admin/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        final String token;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        token = authHeader.substring(7);

        Claims claims;
        try {
            claims = jwtUtils.extractAllClaims(token);
        } catch (Exception e) {
            filterChain.doFilter(request, response);
            return;
        }

        username = claims.getSubject();
        String Role = claims.get("role", String.class);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = null;

            if ("USER".equalsIgnoreCase(Role)) {
                userDetails = userDetailsService.loadUserByUsername(username);
            } else if ("ADMIN".equalsIgnoreCase(Role)) {
                userDetails = adminDetailsService.loadUserByUsername(username);
            }

            if (userDetails != null && jwtUtils.isTokenValid(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
                request.setAttribute("userType", Role);
            }
        }

        filterChain.doFilter(request, response);
    }
}
