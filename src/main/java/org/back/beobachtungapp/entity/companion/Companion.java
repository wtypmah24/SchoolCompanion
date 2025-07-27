package org.back.beobachtungapp.entity.companion;

import java.time.Instant;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Data
public class Companion {

  private long id;
  private String name;
  private String surname;
  private String email;
  private String password;
  private String tgId;
  private String organization;
  @LastModifiedDate Instant updatedAt;
  @CreatedDate() Instant createdAt;
}
