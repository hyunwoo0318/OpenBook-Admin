package Project.OpenBook.Domain.Round.Service.dto;

import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RoundDto {

  @Min(value = 1, message = "회차 번호를 입력해주세요.")
  private Integer number;

  @Min(value = 1, message = "회차 년도를 입력해주세요.")
  private Integer date;
}
