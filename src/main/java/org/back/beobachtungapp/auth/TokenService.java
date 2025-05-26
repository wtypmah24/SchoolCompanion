package org.back.beobachtungapp.auth;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.config.properties.JwtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TokenService {
  private final JwtEncoder encoder;

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  private final JwtProperties jwtProperties;

  @Autowired
  public TokenService(JwtEncoder encoder, JwtProperties jwtProperties) {
    this.encoder = encoder;
    this.jwtProperties = jwtProperties;
  }

  public String generateToken(Authentication authentication) {
    Instant now = Instant.now();
    String username = authentication.getName();
    String role =
        authentication.getAuthorities().stream()
            .findFirst()
            .map(GrantedAuthority::getAuthority)
            .orElse("");

    JwtClaimsSet claims =
        JwtClaimsSet.builder()
            .issuer("self")
            .issuedAt(now)
            .expiresAt(
                now.plus(
                    jwtProperties.getExpiration(),
                    ChronoUnit.HOURS)) // TODO: add refresh token instead
            .subject(username)
            .claim("role", role)
            .build();

    return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
  }
}
