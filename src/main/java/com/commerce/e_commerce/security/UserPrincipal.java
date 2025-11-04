package com.commerce.e_commerce.security;

import com.commerce.e_commerce.domain.security.Role;
import com.commerce.e_commerce.domain.security.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Domain User -> Spring Security UserDetails map’i.
 */
public final class UserPrincipal implements UserDetails {

    private final UUID id;
    private final String email;
    private final String passwordHash;
    private final boolean enabled;
    private final Set<GrantedAuthority> authorities;

    private UserPrincipal(UUID id,
                          String email,
                          String passwordHash,
                          boolean enabled,
                          Set<GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.enabled = enabled;
        this.authorities = Collections.unmodifiableSet(authorities);
    }

    public static UserPrincipal from(User u) {
        if (u == null) throw new IllegalArgumentException("User cannot be null");

        Set<GrantedAuthority> auths = u.getRoles() == null ? Set.of()
                : u.getRoles().stream()
                .map(Role::getName)
                .filter(Objects::nonNull)
                .map(UserPrincipal::toAuthority)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        // SoftDeletable muhtemelen UUID id içeriyor
        UUID id = u.getId(); // SoftDeletable’dan geliyor olmalı
        return new UserPrincipal(
                id,
                u.getEmail(),
                u.getPasswordHash(),
                u.isEnabled(),
                auths
        );
    }

    private static SimpleGrantedAuthority toAuthority(String roleName) {
        String name = roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName;
        return new SimpleGrantedAuthority(name);
    }

    // ---- Extra getter’lar (ihtiyaç olursa)
    public UUID getId() { return id; }
    public String getEmail() { return email; }

    // ---- UserDetails
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public String getPassword() { return passwordHash; }
    @Override public String getUsername() { return email; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return enabled; }
}
