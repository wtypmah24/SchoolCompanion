package org.back.beobachtungapp.entity.companion;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public class CompanionAuthentication extends UsernamePasswordAuthenticationToken {
    private final Companion companion;

    public CompanionAuthentication(Companion companion, List<GrantedAuthority> authorities) {
        super(companion.getEmail(), null, authorities);
        this.companion = companion;
    }

    @Override
    public Object getPrincipal() {
        return companion;
    }
}
