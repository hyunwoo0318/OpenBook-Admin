package Project.OpenBook.Domain.Topic.Service.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TopicTitleListDto {
  private List<String> topicList;
}
