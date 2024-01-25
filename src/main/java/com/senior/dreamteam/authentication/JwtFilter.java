package com.senior.dreamteam.authentication;

import com.senior.dreamteam.entities.User;
import com.senior.dreamteam.exception.DemoGraphqlException;
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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        if(token != null) {
//            token = token.substring(7);
            String username = jwtTokenUtil.getUsernameFromToken(token);
            User user = userService.findUserByEmail(username);
            if (user != null && jwtTokenUtil.isTokenValid(token, user)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null, user.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                log.info("user {} perform some action", username);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            }
        }
        filterChain.doFilter(request, response);
    }
}