package Project.OpenBook.Domain.Timeline.Repo;

import Project.OpenBook.Domain.Timeline.Domain.Timeline;

import java.util.List;

public interface TimelineRepositoryCustom {

  public List<Timeline> queryTimelinesWithEra();

  public List<Timeline> queryTimelinesWithEraAndjjhList();

  public List<Timeline> queryAllForInit();
}
