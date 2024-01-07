package Project.OpenBook.Domain.Chapter.Service.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChapterQueryAdminDto {

  @NotBlank(message = "단원제목을 입력해주세요.")
  private String title;

  @Min(value = 1, message = "단원 번호를 입력해주세요.")
  private int number;

  private String dateComment;
  private Long id;
}
