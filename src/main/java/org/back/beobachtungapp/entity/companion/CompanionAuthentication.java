package org.back.beobachtungapp.entity.companion;

import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public class CompanionAuthentication extends UsernamePasswordAuthenticationToken {
    private final CompanionDto companion;

    public CompanionAuthentication(CompanionDto companion, List<GrantedAuthority> authorities) {
        super(companion.email(), null, authorities);
        this.companion = companion;
    }

    @Override
    public Object getPrincipal() {
        return companion;
    }
}
