package org.back.beobachtungapp.entity.monitoring;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.back.beobachtungapp.entity.companion.Companion;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@SuppressFBWarnings
@Entity
@Table(name = "monitoring_parameters")
@Data
@ToString(exclude = {"companion"})
@EqualsAndHashCode(exclude = {"companion"})
@EntityListeners(AuditingEntityListener.class)
public class MonitoringParameter {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column() String title;

  @Enumerated(EnumType.STRING)
  ScaleType type;

  @Column() String description;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "companion_id")
  private Companion companion;

  @Column() private int minValue;
  @Column() private int maxValue;

  @OneToMany(mappedBy = "monitoringParameter", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<MonitoringEntry> monitoringEntries;

  @LastModifiedDate
  @Column(name = "updated_at")
  Instant updatedAt;

  @CreatedDate()
  @Column(name = "created_at", updatable = false)
  Instant createdAt;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof MonitoringParameter that)) return false;
    return id == that.id;
  }

  @Override
  public int hashCode() {
    return Long.hashCode(id);
  }
}
