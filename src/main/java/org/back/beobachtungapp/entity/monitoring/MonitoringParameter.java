package org.back.beobachtungapp.entity.monitoring;

import jakarta.persistence.*;
import lombok.Data;
import org.back.beobachtungapp.entity.child.Child;

import java.util.Set;

@Entity
@Table(name = "monitoring_parameters")
@Data
public class MonitoringParameter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column()
    String title;
    @Enumerated(EnumType.STRING)
    ScaleType scaleType;
    @Column()
    String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id")
    private Child child;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MonitoringEntry> monitoringEntries;
}
