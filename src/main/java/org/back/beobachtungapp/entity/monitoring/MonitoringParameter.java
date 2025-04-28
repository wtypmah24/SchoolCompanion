package org.back.beobachtungapp.entity.monitoring;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.*;
import java.util.Set;
import lombok.Data;
import org.back.beobachtungapp.entity.child.Child;

@SuppressFBWarnings
@Entity
@Table(name = "monitoring_parameters")
@Data
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
}
