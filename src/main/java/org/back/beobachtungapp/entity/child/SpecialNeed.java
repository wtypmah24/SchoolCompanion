package org.back.beobachtungapp.entity.child;

import jakarta.persistence.*;
import lombok.Data;

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
    @JoinColumn(name = "child_id")
    private Child child;
}
