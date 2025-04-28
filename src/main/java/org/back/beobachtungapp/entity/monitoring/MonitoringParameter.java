package org.back.beobachtungapp.entity.monitoring;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Set;
import lombok.Data;
import org.back.beobachtungapp.entity.child.Child;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@SuppressFBWarnings
@Entity
@Table(name = "monitoring_parameters")
@Data
@EntityListeners(AuditingEntityListener.class)
public class MonitoringParameter {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column() String title;

  @Enumerated(EnumType.STRING)
  ScaleType scaleType;

  @Column() String description;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "child_id")
  private Child child;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<MonitoringEntry> monitoringEntries;

  @LastModifiedDate
  @Column(name = "updated_at")
  LocalDateTime updatedAt;

  @CreatedDate()
  @Column(name = "created_at", updatable = false)
  LocalDateTime createdAt;
}
