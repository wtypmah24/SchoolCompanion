package org.back.beobachtungapp.entity.monitoring;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.back.beobachtungapp.entity.child.Child;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@SuppressFBWarnings
@Entity
@Table(name = "monitoring_entries")
@Data
@ToString(exclude = {"child"})
@EqualsAndHashCode(exclude = {"child"})
@EntityListeners(AuditingEntityListener.class)
public class MonitoringEntry {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column() String value;
  @Column() String notes;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn()
  private MonitoringParameter monitoringParameter;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn()
  private Child child;

  @LastModifiedDate
  @Column(name = "updated_at")
  LocalDateTime updatedAt;

  @CreatedDate()
  @Column(name = "created_at", updatable = false)
  LocalDateTime createdAt;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof MonitoringEntry that)) return false;
    return id == that.id;
  }

  @Override
  public int hashCode() {
    return Long.hashCode(id);
  }
}
