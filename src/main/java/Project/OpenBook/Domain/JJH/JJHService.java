package Project.OpenBook.Domain.JJH;

import static Project.OpenBook.Constants.ErrorCode.CHAPTER_NOT_FOUND;
import static Project.OpenBook.Constants.ErrorCode.TIMELINE_NOT_FOUND;

import Project.OpenBook.Constants.ContentConst;
import Project.OpenBook.Domain.Chapter.Domain.Chapter;
import Project.OpenBook.Domain.Chapter.Repo.ChapterRepository;
import Project.OpenBook.Domain.JJH.JJHContent.JJHContent;
import Project.OpenBook.Domain.JJH.JJHContent.JJHContentRepository;
import Project.OpenBook.Domain.JJH.JJHContentProgress.JJHContentProgressRepository;
import Project.OpenBook.Domain.JJH.JJHList.JJHList;
import Project.OpenBook.Domain.JJH.JJHList.JJHListRepository;
import Project.OpenBook.Domain.JJH.JJHListProgress.JJHListProgressRepository;
import Project.OpenBook.Domain.JJH.dto.*;
import Project.OpenBook.Domain.Timeline.Domain.Timeline;
import Project.OpenBook.Domain.Timeline.Repo.TimelineRepository;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Handler.Exception.CustomException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JJHService {

  private final ChapterRepository chapterRepository;
  private final TimelineRepository timelineRepository;
  private final JJHListRepository jjhListRepository;
  private final JJHListProgressRepository jjhListProgressRepository;

  private final JJHContentRepository jjhContentRepository;
  private final JJHContentProgressRepository jjhContentProgressRepository;

  @Transactional(readOnly = true)
  public JJHListAdminQueryDto queryJJHAdmin() {
    List<ChapterJJHAdminQueryDto> chapterList =
        chapterRepository.queryChaptersWithjjhList().stream()
            .map(
                c -> {
                  Integer jjhNumber =
                      (!c.getJjhLists().isEmpty()) ? c.getJjhLists().get(0).getNumber() : 1000;
                  return new ChapterJJHAdminQueryDto(
                      c.getNumber(), c.getTitle(), jjhNumber, c.getId());
                })
            .sorted(Comparator.comparing(ChapterJJHAdminQueryDto::getJjhNumber))
            .collect(Collectors.toList());
    List<TimelineJJHAdminQueryDto> timelineList =
        timelineRepository.queryTimelinesWithEraAndjjhList().stream()
            .map(
                t -> {
                  Integer jjhNumber =
                      (!t.getJjhLists().isEmpty()) ? t.getJjhLists().get(0).getNumber() : 1000;
                  return new TimelineJJHAdminQueryDto(
                      t.getTitle(),
                      t.getEra().getName(),
                      t.getStartDate(),
                      t.getEndDate(),
                      jjhNumber,
                      t.getId());
                })
            .sorted(Comparator.comparing(TimelineJJHAdminQueryDto::getJjhNumber))
            .collect(Collectors.toList());

    return new JJHListAdminQueryDto(chapterList, timelineList);
  }

  @Transactional
  public void updateJJHList(JJHListUpdateDto dto) {

    Integer chapterType = -1;
    Integer timelineType = 1;
    Map<jjhListType, JJHList> m = new HashMap<>();

    List<JJHUpdateDto> chapterList = dto.getChapterList();
    List<JJHUpdateDto> timelineList = dto.getTimelineList();

    List<JJHList> jjhLists = jjhListRepository.queryJJHListsWithChapterAndTimeline();
    for (JJHList jjhList : jjhLists) {
      if (jjhList.getChapter() == null && jjhList.getTimeline() != null) {
        m.put(new jjhListType(timelineType, jjhList.getTimeline().getId()), jjhList);
      } else if (jjhList.getTimeline() == null && jjhList.getChapter() != null) {
        m.put(new jjhListType(chapterType, jjhList.getChapter().getId()), jjhList);
      } else {
        jjhListRepository.delete(jjhList);
      }
    }

    for (JJHUpdateDto jjhUpdateDto : chapterList) {
      Long chapterId = jjhUpdateDto.getId();
      Integer jjhNumber = jjhUpdateDto.getJjhNumber();
      jjhNumber += 1;
      JJHList jjhList = m.get(new jjhListType(chapterType, jjhUpdateDto.getId()));
      if (jjhList == null) {
        Chapter chapter =
            chapterRepository
                .findById(chapterId)
                .orElseThrow(
                    () -> {
                      throw new CustomException(CHAPTER_NOT_FOUND);
                    });
        JJHList newJJHList = new JJHList(jjhNumber, chapter);
        jjhListRepository.save(newJJHList);
      } else {
        jjhList.updateNumber(jjhNumber);
      }
    }

    for (JJHUpdateDto jjhUpdateDto : timelineList) {
      Long timelineId = jjhUpdateDto.getId();
      Integer jjhNumber = jjhUpdateDto.getJjhNumber();
      jjhNumber += 1;
      JJHList jjhList = m.get(new jjhListType(timelineType, jjhUpdateDto.getId()));
      if (jjhList == null) {
        Timeline timeline =
            timelineRepository
                .findById(timelineId)
                .orElseThrow(
                    () -> {
                      throw new CustomException(TIMELINE_NOT_FOUND);
                    });
        JJHList newJJHList = new JJHList(jjhNumber, timeline);
        jjhListRepository.save(newJJHList);
      } else {
        jjhList.updateNumber(jjhNumber);
      }
    }

    jjhLists.sort(Comparator.comparing(JJHList::getNumber));

    updateJJHContent();
  }

  @Transactional
  public void updateJJHContent() {
    Integer idx = 1;

    Map<jjhContentType, JJHContent> m = new HashMap<>();
    List<JJHContent> jjhContents = jjhContentRepository.queryJJHContents();
    for (JJHContent jjhContent : jjhContents) {
      if (jjhContent.getTopic() != null) {
        m.put(
            new jjhContentType(jjhContent.getContent(), jjhContent.getTopic().getId()), jjhContent);
      } else if (jjhContent.getTimeline() != null) {
        m.put(
            new jjhContentType(jjhContent.getContent(), jjhContent.getTimeline().getId()),
            jjhContent);
      } else if (jjhContent.getChapter() != null) {
        m.put(
            new jjhContentType(jjhContent.getContent(), jjhContent.getChapter().getId()),
            jjhContent);
      }
    }
    List<JJHList> jjhLists =
        jjhListRepository.queryJJHListsWithChapterAndTimeline().stream()
            .sorted(Comparator.comparing(JJHList::getNumber))
            .collect(Collectors.toList());
    for (JJHList jjhList : jjhLists) {
      Chapter chapter = jjhList.getChapter();
      Timeline timeline = jjhList.getTimeline();
      if (chapter != null) {
        // 1. 단원학습 체크
        JJHContent chapterInfoJJHContent =
            m.get(new jjhContentType(ContentConst.CHAPTER_INFO, chapter.getId()));
        if (chapter.getContent() != null) {
          if (chapterInfoJJHContent == null) {
            JJHContent newJJHContent =
                new JJHContent(ContentConst.CHAPTER_INFO, idx++, jjhList, chapter);
            jjhContentRepository.save(newJJHContent);
          } else {
            chapterInfoJJHContent.updateNumber(idx++);
          }
        } else {
          if (chapterInfoJJHContent != null) {
            jjhContentRepository.delete(chapterInfoJJHContent);
          }
        }

        // 2. 단원 내 토픽들 체크
        List<Topic> topicList = chapter.getTopicList().stream()
            .sorted(Comparator.comparing(Topic::getNumber))
            .collect(Collectors.toList());
        for (Topic topic : topicList) {
          JJHContent jjhContent =
              m.get(new jjhContentType(ContentConst.TOPIC_STUDY, topic.getId()));
          if (jjhContent == null) {
            JJHContent newJJHContent =
                new JJHContent(ContentConst.TOPIC_STUDY, idx++, jjhList, topic);
            jjhContentRepository.save(newJJHContent);
          } else {
            jjhContent.updateNumber(idx++);
          }
        }

        // 3. 단원 마무리 문제 체크
        JJHContent jjhContent =
            m.get(new jjhContentType(ContentConst.CHAPTER_COMPLETE_QUESTION, chapter.getId()));
        if (jjhContent == null) {
          JJHContent newJJHContent =
              new JJHContent(ContentConst.CHAPTER_COMPLETE_QUESTION, idx++, jjhList, chapter);
          jjhContentRepository.save(newJJHContent);
        } else {
          jjhContent.updateNumber(idx++);
        }

      } else if (timeline != null) {
        // 4. 연표학습 체크
        JJHContent timelineJJHContent =
            m.get(new jjhContentType(ContentConst.TIMELINE_STUDY, timeline.getId()));
        if (timelineJJHContent == null) {
          JJHContent newJJHContent =
              new JJHContent(ContentConst.TIMELINE_STUDY, idx++, jjhList, timeline);
          jjhContentRepository.save(newJJHContent);
        } else {
          timelineJJHContent.updateNumber(idx++);
        }
      }
    }


  }

  @AllArgsConstructor
  @EqualsAndHashCode
  private class jjhListType {
    public Integer type;
    public Long id;
  }

  @AllArgsConstructor
  @EqualsAndHashCode
  private class jjhContentType {
    public ContentConst content;
    public Long id;
  }
}
