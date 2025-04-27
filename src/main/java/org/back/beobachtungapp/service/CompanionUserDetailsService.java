package org.back.beobachtungapp.service;

import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.entity.companion.Companion;
import org.back.beobachtungapp.repository.CompanionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
class CompanionUserDetailsService implements UserDetailsService {
    private final CompanionRepository companionRepository;

    @Autowired
    public CompanionUserDetailsService(CompanionRepository companionRepository) {
        this.companionRepository = companionRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Companion companion = companionRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Companion not found with email: " + email));
        return new User(
                companion.getEmail(),
                companion.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")) // or companion.getRole()
        );
    }
}
