package Project.OpenBook.Domain.Topic.Service;

import static Project.OpenBook.Constants.ErrorCode.*;

import Project.OpenBook.Domain.Chapter.Domain.Chapter;
import Project.OpenBook.Domain.Chapter.Repo.ChapterRepository;
import Project.OpenBook.Domain.Choice.Domain.Choice;
import Project.OpenBook.Domain.Description.Domain.Description;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.QuestionCategory.Domain.QuestionCategory;
import Project.OpenBook.Domain.QuestionCategory.Repo.QuestionCategoryRepository;
import Project.OpenBook.Domain.Search.TopicSearch.TopicSearch;
import Project.OpenBook.Domain.Search.TopicSearch.TopicSearchRepository;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Domain.Topic.Service.dto.PrimaryDateDto;
import Project.OpenBook.Domain.Topic.Service.dto.TopicAddUpdateDto;
import Project.OpenBook.Domain.Topic.Service.dto.TopicNumberDto;
import Project.OpenBook.Domain.Topic.TopicPrimaryDate.Domain.TopicPrimaryDate;
import Project.OpenBook.Domain.Topic.TopicPrimaryDate.Repository.TopicPrimaryDateRepository;
import Project.OpenBook.Handler.Exception.CustomException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TopicService {

  private final TopicRepository topicRepository;
  private final ChapterRepository chapterRepository;
  private final TopicPrimaryDateRepository topicPrimaryDateRepository;
  private final TopicSearchRepository topicSearchRepository;
  private final QuestionCategoryRepository questionCategoryRepository;
  private final TopicValidator topicValidator;

  public Topic queryTopicWithCategoryChapterEra(String topicTitle) {
    return topicRepository
        .queryTopicWithQuestionCategory(topicTitle)
        .orElseThrow(
            () -> {
              throw new CustomException(TOPIC_NOT_FOUND);
            });
  }

  @Transactional
  public void createTopic(TopicAddUpdateDto topicAddUpdateDto) {

    Long questionCategoryId = topicAddUpdateDto.getQuestionCategory().getId();
    QuestionCategory questionCategory =
        questionCategoryRepository
            .findById(questionCategoryId)
            .orElseThrow(
                () -> {
                  throw new CustomException(QUESTION_CATEGORY_NOT_FOUND);
                });
    Chapter chapter = checkChapter(topicAddUpdateDto.getChapter());
    topicValidator.checkDupTopicTitle(topicAddUpdateDto.getTitle());

    // 토픽 저장
    Topic topic =
        Topic.builder()
            .chapter(chapter)
            .questionCategory(questionCategory)
            .dateComment(topicAddUpdateDto.getDateComment())
            .title(topicAddUpdateDto.getTitle())
            .detail(topicAddUpdateDto.getDetail())
            .number(topicAddUpdateDto.getNumber())
            .questionNum(0)
            .choiceNum(0)
            .build();
    topicRepository.save(topic);
    topicSearchRepository.save(new TopicSearch(topic));
    // 연표에 표시할 날짜 저장
    List<PrimaryDateDto> dateList = new ArrayList<>();
    if (topicAddUpdateDto.getExtraDateList() != null) {
      dateList = topicAddUpdateDto.getExtraDateList();
    }
    List<TopicPrimaryDate> topicPrimaryDateList =
        dateList.stream()
            .map(d -> new TopicPrimaryDate(d.getExtraDate(), d.getExtraDateComment(), topic))
            .collect(Collectors.toList());
    topicPrimaryDateRepository.saveAll(topicPrimaryDateList);
  }

  @Transactional
  public void updateTopic(String topicTitle, TopicAddUpdateDto topicAddUpdateDto) {
    Topic topic = topicValidator.checkTopic(topicTitle);
    String inputTitle = topicAddUpdateDto.getTitle();

    if (!topicTitle.equals(inputTitle)) {
      // 새로 입력받은 제목이 중복되는지 확인
      topicValidator.checkDupTopicTitle(inputTitle);
    }

    int chapterNum = topicAddUpdateDto.getChapter();
    Chapter chapter = checkChapter(chapterNum);

    Long questionCategoryId = topicAddUpdateDto.getQuestionCategory().getId();
    QuestionCategory questionCategory =
        questionCategoryRepository
            .findById(questionCategoryId)
            .orElseThrow(
                () -> {
                  throw new CustomException(QUESTION_CATEGORY_NOT_FOUND);
                });

    // 토픽 수정
    Topic updatedTopic =
        topic.updateTopic(
            topicAddUpdateDto.getNumber(),
            topicAddUpdateDto.getTitle(),
            topicAddUpdateDto.getDateComment(),
            topicAddUpdateDto.getDetail(),
            chapter,
            questionCategory);

    topicSearchRepository.save(new TopicSearch(updatedTopic));

    // 연표에 나올 날짜 수정
    List<TopicPrimaryDate> prevDateList = topic.getTopicPrimaryDateList();
    topicPrimaryDateRepository.deleteAllInBatch(prevDateList);
    List<TopicPrimaryDate> topicPrimaryDateList =
        topicAddUpdateDto.getExtraDateList().stream()
            .map(d -> new TopicPrimaryDate(d.getExtraDate(), d.getExtraDateComment(), topic))
            .collect(Collectors.toList());
    topicPrimaryDateRepository.saveAll(topicPrimaryDateList);
  }

  @Transactional
  public void deleteTopic(String topicTitle) {
    Topic findTopic = queryTopicWithCategoryChapterEra(topicTitle);

    List<Choice> choiceList = findTopic.getChoiceList();
    if (!choiceList.isEmpty()) {
      throw new CustomException(TOPIC_HAS_CHOICE);
    }
    List<Description> descriptionList = findTopic.getDescriptionList();
    if (!descriptionList.isEmpty()) {
      throw new CustomException(TOPIC_HAS_DESCRIPTION);
    }
    List<Keyword> keywordList = findTopic.getKeywordList();
    if (!keywordList.isEmpty()) {
      throw new CustomException(TOPIC_HAS_KEYWORD);
    }

    List<TopicPrimaryDate> topicPrimaryDateList = findTopic.getTopicPrimaryDateList();
    topicPrimaryDateRepository.deleteAllInBatch(topicPrimaryDateList);

    topicRepository.delete(findTopic);
    topicSearchRepository.deleteById(findTopic.getId());
  }

  private Chapter checkChapter(int num) {
    return chapterRepository
        .findOneByNumber(num)
        .orElseThrow(
            () -> {
              throw new CustomException(CHAPTER_NOT_FOUND);
            });
  }

  @Transactional
  public void updateTopicNumber(List<TopicNumberDto> topicNumberDtoList) {
    Map<String, Integer> m = new HashMap<>();
    List<String> topicTitleList = new ArrayList<>();

    for (TopicNumberDto topicNumberDto : topicNumberDtoList) {
      String topicTitle = topicNumberDto.getTitle();
      Integer topicNumber = topicNumberDto.getNumber();
      m.put(topicTitle, topicNumber);
      topicTitleList.add(topicTitle);
    }

    List<Topic> topicList = topicRepository.queryTopicsByTopicTitleList(topicTitleList);
    if (topicList.size() != topicTitleList.size()) {
      throw new CustomException(TOPIC_NOT_FOUND);
    }

    for (Topic topic : topicList) {
      Integer number = m.get(topic.getTitle());
      topic.updateTopicNumber(number);
    }
  }
}
