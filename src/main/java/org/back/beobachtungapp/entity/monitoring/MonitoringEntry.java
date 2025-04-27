package org.back.beobachtungapp.entity.monitoring;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@SuppressFBWarnings
@Entity
@Table()
@Data
public class MonitoringEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column()
    String value;
    @Column()
    LocalDate date;
    @Column()
    String notes;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn()
    private MonitoringParameter monitoringParameter;
}
