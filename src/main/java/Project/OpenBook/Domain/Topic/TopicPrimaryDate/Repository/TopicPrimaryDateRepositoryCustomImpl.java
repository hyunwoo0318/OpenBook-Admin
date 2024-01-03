package Project.OpenBook.Domain.Topic.TopicPrimaryDate.Repository;

import Project.OpenBook.Domain.Topic.TopicPrimaryDate.Domain.TopicPrimaryDate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static Project.OpenBook.Domain.Era.QEra.era;
import static Project.OpenBook.Domain.QuestionCategory.Domain.QQuestionCategory.questionCategory;
import static Project.OpenBook.Domain.Topic.Domain.QTopic.topic;
import static Project.OpenBook.Domain.Topic.TopicPrimaryDate.Domain.QTopicPrimaryDate.topicPrimaryDate;

@Repository
@RequiredArgsConstructor
public class TopicPrimaryDateRepositoryCustomImpl implements TopicPrimaryDateRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public List<TopicPrimaryDate> queryAllForInit() {
        return queryFactory.selectFrom(topicPrimaryDate).distinct()
                .leftJoin(topicPrimaryDate.topic, topic).fetchJoin()
                .leftJoin(topic.questionCategory, questionCategory).fetchJoin()
                .leftJoin(questionCategory.era, era).fetchJoin()
                .fetch();
    }
}
