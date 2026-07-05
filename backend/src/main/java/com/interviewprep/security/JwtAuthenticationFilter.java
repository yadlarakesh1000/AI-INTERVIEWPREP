package com.interviewprep.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.interviewprep.common.AppConstants;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    // UserDetailsService does not exist until STEP 009. ObjectProvider resolves it
    // lazily and never fails at startup when no bean is present.
    private final ObjectProvider<UserDetailsService> userDetailsServiceProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = resolveToken(request);
            if (token != null && jwtTokenProvider.validateToken(token)) {
                String email = jwtTokenProvider.getEmailFromToken(token);
                UsernamePasswordAuthenticationToken authentication = buildAuthentication(email);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            // Never break the filter chain on auth errors; let the request proceed
            // unauthenticated and be rejected downstream by the entry point.
            log.warn("Could not set user authentication in security context: {}", ex.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken buildAuthentication(String email) {
        UserDetailsService userDetailsService = userDetailsServiceProvider.getIfAvailable();
        if (userDetailsService != null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            return new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
        }
        // Fallback until CustomUserDetailsService exists: authenticate with the email
        // principal and no granted authorities (there are no roles in this app).
        return new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
    }

    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(AppConstants.AUTH_HEADER);
        if (header != null && header.startsWith(AppConstants.TOKEN_PREFIX)) {
            return header.substring(AppConstants.TOKEN_PREFIX.length());
        }
        return null;
    }
}
