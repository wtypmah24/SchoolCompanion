package org.back.beobachtungapp.entity.companion;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.ToString;
import org.back.beobachtungapp.entity.child.Child;
import org.back.beobachtungapp.entity.monitoring.MonitoringParameter;
import org.back.beobachtungapp.entity.task.Task;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

// TODO: add roles
@SuppressFBWarnings
@Entity
@Table(name = "companions")
@Data
@EntityListeners(AuditingEntityListener.class)
public class Companion {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String surname;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(unique = true)
  private String tgId;

  @Column() private String organization;

  @ElementCollection
  @CollectionTable(name = "companion_thread_ids", joinColumns = @JoinColumn(name = "companion_id"))
  @Column(name = "thread_id")
  private Set<String> chatIds = new HashSet<>();

  @OneToMany(
      mappedBy = "schoolCompanion",
      cascade = CascadeType.REMOVE,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  @ToString.Exclude
  private Set<Child> children = new HashSet<>();

  @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
  @ToString.Exclude
  private Set<Task> tasks = new HashSet<>();

  @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
  @ToString.Exclude
  private Set<MonitoringParameter> params = new HashSet<>();

  @LastModifiedDate
  @Column(name = "updated_at")
  Instant updatedAt;

  @CreatedDate()
  @Column(name = "created_at", updatable = false)
  Instant createdAt;
}
