package Project.OpenBook.Domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public abstract class BaseEntity {

  @CreatedDate private String createdTime;
  @LastModifiedDate private String modifiedTime;

  @PrePersist
  public void prePersist() {
    this.createdTime =
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss"));
    this.modifiedTime = this.createdTime;
  }

  @PreUpdate
  public void preUpdate() {
    this.modifiedTime =
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss"));
  }
}
