package Project.OpenBook.Domain.QuestionCategory.Repo;

import static Project.OpenBook.Domain.Category.Domain.QCategory.category;
import static Project.OpenBook.Domain.Era.QEra.era;
import static Project.OpenBook.Domain.QuestionCategory.Domain.QQuestionCategory.questionCategory;

import Project.OpenBook.Domain.QuestionCategory.Domain.QuestionCategory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QuestionCategoryRepositoryCustomImpl implements QuestionCategoryRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<QuestionCategory> queryQuestionCategoriesForAdmin() {
    return queryFactory
        .selectFrom(questionCategory)
        .distinct()
        .leftJoin(questionCategory.category, category)
        .fetchJoin()
        .leftJoin(questionCategory.era, era)
        .fetchJoin()
        .leftJoin(questionCategory.topicList)
        .fetchJoin()
        .fetch();
  }
}
