package org.back.beobachtungapp.entity.child;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.*;
import lombok.Data;

@SuppressFBWarnings
@Entity
@Table()
@Data
public class SpecialNeed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column()
    private String type;
    @Column()
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn()
    private Child child;
}
