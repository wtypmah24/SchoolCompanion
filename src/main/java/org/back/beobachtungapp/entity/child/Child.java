package org.back.beobachtungapp.entity.child;

import java.time.Instant;
import java.time.LocalDate;
import lombok.Data;

@Data
public class Child {
  private Long id;
  private String name;
  private String surname;
  private String email;
  private String phoneNumber;
  private LocalDate dateOfBirth;
  private boolean active;
  private Long companionId;
  private Instant updatedAt;
  private Instant createdAt;
}
