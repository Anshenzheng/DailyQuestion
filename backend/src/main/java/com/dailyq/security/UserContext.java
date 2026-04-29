package com.dailyq.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserContext {

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            return principal.getUserId();
        }
        return null;
    }

    public static boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            return principal.isAdmin();
        }
        return false;
    }

    @Data
    @AllArgsConstructor
    public static class UserPrincipal {
        private Long userId;
        private boolean isAdmin;
    }
}
