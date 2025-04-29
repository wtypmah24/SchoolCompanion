package org.back.beobachtungapp.entity.note;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.*;
import lombok.Data;
import org.back.beobachtungapp.entity.child.Child;
import org.back.beobachtungapp.entity.companion.Companion;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@SuppressFBWarnings
@Entity
@Table()
@Data
@EntityListeners(AuditingEntityListener.class)
public class Note {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column() String content;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "child_id")
  private Child child;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "companion_id")
  private Companion companion;

  @LastModifiedDate
  @Column(name = "updated_at")
  LocalDateTime updatedAt;

  @CreatedDate()
  @Column(name = "created_at", updatable = false)
  LocalDateTime createdAt;
}
