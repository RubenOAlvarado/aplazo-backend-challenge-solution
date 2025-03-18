package com.bnpl.rubalv.filter;

import com.bnpl.rubalv.service.JwtTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenService jwtTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if(header != null && header.startsWith("Bearer ")){
            String token = header.substring(7);

            try{
                Authentication auth = jwtTokenService.validateToken(token);
                if (auth != null) {
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    log.debug("JWT authentication successful for user: {}",
                            auth.getName());
                }
            } catch (Exception e) {
                log.error("Error processing JWT authentication: {}", e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }
}
