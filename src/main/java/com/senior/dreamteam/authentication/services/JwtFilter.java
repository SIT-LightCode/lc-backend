package com.senior.dreamteam.authentication.services;

import com.senior.dreamteam.authentication.services.JwtTokenUtil;
import com.senior.dreamteam.entities.User;
import com.senior.dreamteam.services.TokenService;
import com.senior.dreamteam.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    final JwtTokenUtil jwtTokenUtil;

    final UserService userService;
    final TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        if (token != null && !token.isEmpty()) {
             if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            try {
                String username = jwtTokenUtil.getUsernameFromToken(token);
                User user = userService.findUserByEmail(username);
                if (user != null && jwtTokenUtil.isTokenValid(token, user) && jwtTokenUtil.isAccessToken(token)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null, user.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    log.info("user {} perform some action", username);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    log.error("Authentication failed");
                }
            } catch (Exception e) {
                log.error("Authentication failed", e.getMessage());
//                throw new DemoGraphqlException(e.getMessage());
//                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: " + e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }
}