package org.back.beobachtungapp.entity.monitoring;

import java.time.Instant;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Data
public class MonitoringParameter {
  private long id;
  private String title;
  private ScaleType type;
  private String description;
  private Long companionId;
  private int minValue;
  private int maxValue;
  @LastModifiedDate Instant updatedAt;
  @CreatedDate() Instant createdAt;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof MonitoringParameter that)) return false;
    return id == that.id;
  }

  @Override
  public int hashCode() {
    return Long.hashCode(id);
  }
}
