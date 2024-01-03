package Project.OpenBook.Domain.QuestionCategory.Service.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionCategoryQueryCustomerDto {

    private Long id;
    private String title;
    private Integer number;
    private Integer score;
    private Integer topicCount;



}
