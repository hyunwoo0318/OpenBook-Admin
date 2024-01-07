package Project.OpenBook.Config;

import Project.OpenBook.Domain.Chapter.Repo.ChapterRepository;
import Project.OpenBook.Domain.Customer.Repository.CustomerRepository;
import Project.OpenBook.Domain.Customer.Service.CustomerService;
import Project.OpenBook.Domain.Keyword.KeywordPrimaryDate.Domain.KeywordPrimaryDate;
import Project.OpenBook.Domain.Keyword.KeywordPrimaryDate.Repository.KeywordPrimaryDateRepository;
import Project.OpenBook.Domain.Keyword.Repository.KeywordRepository;
import Project.OpenBook.Domain.Search.ChapterSearch.ChapterSearch;
import Project.OpenBook.Domain.Search.ChapterSearch.ChapterSearchRepository;
import Project.OpenBook.Domain.Search.KeywordSearch.KeywordSearch;
import Project.OpenBook.Domain.Search.KeywordSearch.KeywordSearchRepository;
import Project.OpenBook.Domain.Search.TopicSearch.TopicSearch;
import Project.OpenBook.Domain.Search.TopicSearch.TopicSearchRepository;
import Project.OpenBook.Domain.Timeline.Domain.Timeline;
import Project.OpenBook.Domain.Timeline.Repo.TimelineRepository;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Domain.Topic.TopicPrimaryDate.Domain.TopicPrimaryDate;
import Project.OpenBook.Domain.Topic.TopicPrimaryDate.Repository.TopicPrimaryDateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class InitConfig {

    private final CustomerService customerService;

    private final TopicSearchRepository topicSearchRepository;
    private final TopicRepository topicRepository;

    private final ChapterRepository chapterRepository;
    private final ChapterSearchRepository chapterSearchRepository;

    private final KeywordRepository keywordRepository;
    private final KeywordSearchRepository keywordSearchRepository;

    private final TimelineRepository timelineRepository;
    private final KeywordPrimaryDateRepository keywordPrimaryDateRepository;
    private final TopicPrimaryDateRepository topicPrimaryDateRepository;


    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;


    /**
     * ElasticSearch를 위한 init 각 topic의 title, id를 저장
     */
    @Bean
    public void initElasticSearchIndex() {
        chapterSearchRepository.deleteAll();
        topicSearchRepository.deleteAll();
        keywordSearchRepository.deleteAll();

        List<ChapterSearch> chapterSearchList = chapterRepository.findAll().stream()
            .map(ChapterSearch::new)
            .collect(Collectors.toList());

        List<TopicSearch> topicSearchList = topicRepository.queryTopicsWithChapter().stream()
            .map(TopicSearch::new)
            .collect(Collectors.toList());

        List<KeywordSearch> keywordSearchList = keywordRepository.queryKeywordsWithChapter()
            .stream()
            .map(KeywordSearch::new)
            .collect(Collectors.toList());

        chapterSearchRepository.saveAll(chapterSearchList);
        topicSearchRepository.saveAll(topicSearchList);
        keywordSearchRepository.saveAll(keywordSearchList);

    }

    /**
     * 연표 속해있는 날짜 개수 세팅
     */
    @Bean
    public void initTimelineCounts() {
        List<Timeline> timelineList = timelineRepository.queryAllForInit();
        List<KeywordPrimaryDate> keywordPrimaryDateList = keywordPrimaryDateRepository.queryAllForInit();
        List<TopicPrimaryDate> topicPrimaryDateList = topicPrimaryDateRepository.queryAllForInit();
        for (TopicPrimaryDate date : topicPrimaryDateList) {
            for (Timeline timeline : timelineList) {
                if (timeline.getEra() == date.getTopic().getQuestionCategory().getEra() &&
                    timeline.getStartDate() <= date.getExtraDate() &&
                    timeline.getEndDate() >= date.getExtraDate()) {
                    timeline.updateCount();
                    break;
                }
            }
        }

        for (KeywordPrimaryDate date : keywordPrimaryDateList) {
            for (Timeline timeline : timelineList) {
                if (timeline.getEra() == date.getKeyword().getTopic().getQuestionCategory().getEra()
                    &&
                    timeline.getStartDate() <= date.getExtraDate() &&
                    timeline.getEndDate() >= date.getExtraDate()) {
                    timeline.updateCount();
                    break;
                }
            }
        }

    }

    /**
     * 기본 관리자 아이디 세팅
     */
//    @Bean
//    public void initAdmin(){
//        if(customerRepository.findByNickName("admin1").isEmpty()){
//            Customer admin1 = new Customer("admin1", passwordEncoder.encode("admin1"), Role.ADMIN);
//            Customer admin2 = new Customer("admin2", passwordEncoder.encode("admin2"), Role.ADMIN);
//            customerRepository.saveAll(Arrays.asList(admin1, admin2));
//        }
//    }


}
