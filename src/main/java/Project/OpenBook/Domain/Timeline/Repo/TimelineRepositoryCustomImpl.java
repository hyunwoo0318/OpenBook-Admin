package Project.OpenBook.Domain.Timeline.Repo;

import static Project.OpenBook.Domain.Era.QEra.era;
import static Project.OpenBook.Domain.Timeline.Domain.QTimeline.timeline;

import Project.OpenBook.Domain.Timeline.Domain.Timeline;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TimelineRepositoryCustomImpl implements TimelineRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<Timeline> queryTimelinesWithEra() {
    return queryFactory.selectFrom(timeline).leftJoin(timeline.era, era).fetchJoin().fetch();
  }

  @Override
  public List<Timeline> queryTimelinesWithEraAndjjhList() {
    return queryFactory
        .selectFrom(timeline)
        .leftJoin(timeline.era, era)
        .fetchJoin()
        .leftJoin(timeline.jjhLists)
        .fetchJoin()
        .fetch();
  }

  @Override
  public List<Timeline> queryAllForInit() {
    return queryFactory.selectFrom(timeline).leftJoin(timeline.era, era).fetchJoin().fetch();
  }
}
