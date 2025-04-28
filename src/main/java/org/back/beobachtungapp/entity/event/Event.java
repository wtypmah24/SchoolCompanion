package org.back.beobachtungapp.entity.event;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Data;
import org.back.beobachtungapp.entity.companion.Companion;

@SuppressFBWarnings
@Entity
@Table()
@Data
public class Event {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column() String title;
  @Column() String description;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "companion_id")
  private Companion companion;

  @Column() LocalDate eventDate;
}
