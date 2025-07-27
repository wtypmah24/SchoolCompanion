package org.back.beobachtungapp.auth;

import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dao.CompanionDao;
import org.back.beobachtungapp.entity.companion.Companion;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class CompanionUserDetailsService implements UserDetailsService {
  private final CompanionDao companionDao;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Companion companion =
        companionDao
            .findCompanionByEmail(email)
            .orElseThrow(
                () -> new NoSuchElementException("Companion not found with email: " + email));
    return new User(
        companion.getEmail(),
        companion.getPassword(),
        List.of(new SimpleGrantedAuthority("ROLE_USER")) // or companion.getRole()
        );
  }
}
