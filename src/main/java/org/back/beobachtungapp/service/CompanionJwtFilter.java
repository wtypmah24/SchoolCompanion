package org.back.beobachtungapp.service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.entity.companion.Companion;
import org.back.beobachtungapp.entity.companion.CompanionAuthentication;
import org.back.beobachtungapp.mapper.CompanionMapper;
import org.back.beobachtungapp.repository.CompanionRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
public class CompanionJwtFilter extends OncePerRequestFilter {

  private final JwtDecoder jwtDecoder;
  private final CompanionRepository companionRepository;
  private final CompanionMapper companionMapper;

  @Autowired
  public CompanionJwtFilter(
      JwtDecoder jwtDecoder,
      CompanionRepository companionRepository,
      CompanionMapper companionMapper) {
    this.jwtDecoder = jwtDecoder;
    this.companionRepository = companionRepository;
    this.companionMapper = companionMapper;
  }

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
        Companion companion =
            companionRepository
                .findByEmail(email)
                .orElseThrow(
                    () -> new NoSuchElementException("Companion not found with email: " + email));
        CompanionDto companionDto = companionMapper.companionToCompanionDto(companion);
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        CompanionAuthentication authentication =
            new CompanionAuthentication(companionDto, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

      } catch (JwtException e) {
        logger.warn("Failed to decode JWT");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Invalid token\"}");
        return;
      }
    }

    filterChain.doFilter(request, response);
  }
}
