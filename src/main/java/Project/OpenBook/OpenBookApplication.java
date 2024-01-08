package Project.OpenBook;

import Project.OpenBook.Domain.Search.ChapterSearch.ChapterSearchRepository;
import Project.OpenBook.Domain.Search.KeywordSearch.KeywordSearchRepository;
import Project.OpenBook.Domain.Search.TopicSearch.TopicSearchRepository;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(excludeFilters = @ComponentScan.Filter(
    type = FilterType.ASSIGNABLE_TYPE,
    classes = {TopicSearchRepository.class, KeywordSearchRepository.class,
        ChapterSearchRepository.class}))
@EnableBatchProcessing // Batch를 쓰기 위해 필요한 어노테이션

public class OpenBookApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenBookApplication.class, args);
    }
}
