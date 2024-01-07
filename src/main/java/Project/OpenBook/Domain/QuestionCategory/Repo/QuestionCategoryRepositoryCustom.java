package Project.OpenBook.Domain.QuestionCategory.Repo;

import Project.OpenBook.Domain.QuestionCategory.Domain.QuestionCategory;

import java.util.List;

public interface QuestionCategoryRepositoryCustom {
  public List<QuestionCategory> queryQuestionCategoriesForAdmin();
}
