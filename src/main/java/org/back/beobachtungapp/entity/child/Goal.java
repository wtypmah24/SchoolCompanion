package org.back.beobachtungapp.entity.child;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

@SuppressFBWarnings
@Entity
@Table()
@Data
public class Goal {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column() private String description;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "child_id")
  private Child child;

  @CreatedDate() LocalDate createdAt;
}
