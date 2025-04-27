package org.back.beobachtungapp.entity.child;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.*;
import lombok.Data;
import org.back.beobachtungapp.entity.companion.Companion;
import org.back.beobachtungapp.entity.monitoring.MonitoringParameter;
import org.back.beobachtungapp.entity.note.Note;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@SuppressFBWarnings
@Entity
@Table()
@Data
public class Child {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column()
    private String name;
    @Column()
    private String surname;
    @Column()
    private String email;
    @Column()
    private String phoneNumber;
    @ElementCollection
    List<String> interests;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "companion_id")
    private Companion schoolCompanion;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SpecialNeed> specialNeeds;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Goal> goals;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MonitoringParameter> monitoringParameters;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Note> notes;
    @CreatedDate()
    LocalDate createdAt;
}
