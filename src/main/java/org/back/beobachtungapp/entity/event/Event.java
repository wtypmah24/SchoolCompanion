package org.back.beobachtungapp.entity.event;

import java.time.Instant;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Data
public class Event {
  private long id;
  private String title;
  private String description;
  private Long companionID;
  private Long childId;
  private Instant startDateTime;
  private Instant endDateTime;
  private String location;
  @LastModifiedDate private Instant updatedAt;
  @CreatedDate() private Instant createdAt;
}
