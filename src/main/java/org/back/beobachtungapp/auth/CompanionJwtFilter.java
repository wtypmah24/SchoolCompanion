package org.back.beobachtungapp.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dao.CompanionDao;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class CompanionJwtFilter extends OncePerRequestFilter {

  private final JwtDecoder jwtDecoder;
  private final CompanionDao companionDao;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);
      try {
        Jwt jwt = jwtDecoder.decode(token);
        String email = jwt.getSubject();
        CompanionDto companion =
            companionDao
                .findByEmail(email)
                .orElseThrow(
                    () -> new NoSuchElementException("Companion not found with email: " + email));
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        CompanionAuthentication authentication =
            new CompanionAuthentication(companion, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

      } catch (JwtException e) {
        logger.warn("Failed to decode JWT", e);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Invalid token\"}");
        return;
      }
    }

    filterChain.doFilter(request, response);
  }
}
