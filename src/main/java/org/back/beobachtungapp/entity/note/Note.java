package org.back.beobachtungapp.entity.note;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.Data;
import lombok.ToString;
import org.back.beobachtungapp.entity.child.Child;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@SuppressFBWarnings
@Entity
@Table(name = "notes")
@Data
@EntityListeners(AuditingEntityListener.class)
public class Note {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(length = 1000)
  String content;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "child_id")
  @ToString.Exclude
  private Child child;

  @LastModifiedDate
  @Column(name = "updated_at")
  Instant updatedAt;

  @CreatedDate()
  @Column(name = "created_at", updatable = false)
  Instant createdAt;
}
