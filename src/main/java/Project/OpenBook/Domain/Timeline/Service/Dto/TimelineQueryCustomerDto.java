package Project.OpenBook.Domain.Timeline.Service.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TimelineQueryCustomerDto {
  private String title;
  private String era;
  private Integer startDate;
  private Integer endDate;
  private Long id;
  private Integer score;
  private Integer timelineCount;
}
