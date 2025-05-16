package org.back.beobachtungapp.entity.child;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.back.beobachtungapp.entity.companion.Companion;
import org.back.beobachtungapp.entity.event.Event;
import org.back.beobachtungapp.entity.monitoring.MonitoringEntry;
import org.back.beobachtungapp.entity.note.Note;
import org.back.beobachtungapp.entity.task.Task;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@SuppressFBWarnings
@Entity
@Table(name = "children")
@Data
@ToString(exclude = {"events", "goals", "notes", "specialNeeds", "entries", "tasks"})
@EqualsAndHashCode(exclude = {"events", "goals", "notes", "specialNeeds", "entries", "tasks"})
@EntityListeners(AuditingEntityListener.class)
public class Child {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column() private String name;
  @Column() private String surname;
  @Column() private String email;
  @Column() private String phoneNumber;
  @Column() private LocalDate dateOfBirth;
  @ElementCollection List<String> interests;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "companion_id")
  private Companion schoolCompanion;

  @OneToMany(mappedBy = "child", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private Set<SpecialNeed> specialNeeds;

  @OneToMany(mappedBy = "child", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private Set<Goal> goals;

  @OneToMany(mappedBy = "child", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private Set<MonitoringEntry> entries;

  @OneToMany(mappedBy = "child", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private Set<Note> notes;

  @OneToMany(mappedBy = "child", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Event> events;

  @OneToMany(mappedBy = "child", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Task> tasks;

  @LastModifiedDate
  @Column(name = "updated_at")
  Instant updatedAt;

  @CreatedDate()
  @Column(name = "created_at", updatable = false)
  Instant createdAt;

  public void addSpecialNeed(SpecialNeed need) {
    need.setChild(this);
    specialNeeds.add(need);
  }

  public void addNote(Note note) {
    note.setChild(this);
    notes.add(note);
  }

  public void addGoal(Goal goal) {
    goal.setChild(this);
    goals.add(goal);
  }

  public void addMonitoringEntry(MonitoringEntry monitoringEntry) {
    monitoringEntry.setChild(this);
    entries.add(monitoringEntry);
  }

  public void addEvent(Event event) {
    event.setChild(this);
    events.add(event);
  }

  public void addTask(Task task) {
    task.setChild(this);
    tasks.add(task);
  }
}
