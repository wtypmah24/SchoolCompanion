package org.back.beobachtungapp.entity.monitoring;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

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
    @JoinColumn(name = "parameter_id")
    private MonitoringParameter monitoringParameter;
}
