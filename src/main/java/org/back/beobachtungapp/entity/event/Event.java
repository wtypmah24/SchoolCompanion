package org.back.beobachtungapp.entity.event;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;
import org.back.beobachtungapp.entity.companion.Companion;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@SuppressFBWarnings
@Entity
@Table()
@Data
@EntityListeners(AuditingEntityListener.class)
public class Event {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column() String title;
  @Column() String description;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "companion_id")
  private Companion companion;

  @Column() LocalDate eventDate;

  @LastModifiedDate
  @Column(name = "updated_at")
  LocalDateTime updatedAt;

  @CreatedDate()
  @Column(name = "created_at", updatable = false)
  LocalDateTime createdAt;
}
