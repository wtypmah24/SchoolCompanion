package org.back.beobachtungapp.entity.monitoring;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@SuppressFBWarnings
@Entity
@Table(name = "monitoring_entry")
@Data
@EntityListeners(AuditingEntityListener.class)
public class MonitoringEntry {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column() String value;
  @Column() LocalDate date;
  @Column() String notes;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn()
  private MonitoringParameter monitoringParameter;

  @LastModifiedDate
  @Column(name = "updated_at")
  LocalDateTime updatedAt;

  @CreatedDate()
  @Column(name = "created_at", updatable = false)
  LocalDateTime createdAt;
}
