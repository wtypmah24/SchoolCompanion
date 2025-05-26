package org.back.beobachtungapp.entity.session;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.Data;
import org.back.beobachtungapp.entity.companion.Companion;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "work_sessions")
@Data
@EntityListeners(AuditingEntityListener.class)
public class WorkSession {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column() Instant startTime;
  @Column() Instant endTime;
  @Column() String note;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "companion_id")
  Companion companion;

  @LastModifiedDate
  @Column(name = "updated_at")
  Instant updatedAt;

  @CreatedDate()
  @Column(name = "created_at", updatable = false)
  Instant createdAt;
}
