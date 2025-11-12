package com.commerce.e_commerce.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public final class SecurityUtils {
    private SecurityUtils() {}

    public static UUID currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;
        Object principal = auth.getPrincipal();
        if (principal instanceof UUID u) return u;
        // Eğer UserPrincipal'a geçtiysen:
        // if (principal instanceof UserPrincipal up) return up.getId();
        return null;
    }
}
