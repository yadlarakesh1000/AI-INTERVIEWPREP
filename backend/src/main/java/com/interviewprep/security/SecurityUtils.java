package com.interviewprep.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.interviewprep.exception.UnauthorizedException;

/**
 * Helper for reading the currently authenticated user from the SecurityContext.
 * Every controller uses {@link #getCurrentUserId()} to scope operations to the
 * authenticated user (users can only access their own data — Section 10).
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new UnauthorizedException("User is not authenticated");
        }
        return principal.getId();
    }
}
