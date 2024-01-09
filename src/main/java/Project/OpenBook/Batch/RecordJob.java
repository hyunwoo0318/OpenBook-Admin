package Project.OpenBook.Batch;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Customer.Repository.CustomerRepository;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.LearningRecord.KeywordLearningRecord.Domain.KeywordLearningRecord;
import Project.OpenBook.Domain.LearningRecord.KeywordLearningRecord.Repo.KeywordLearningRecordRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class RecordJob {

  private final KeywordLearningRecordRepository keywordLearningRecordRepository;

  private final CustomerRepository customerRepository;

  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final EntityManagerFactory entityManagerFactory;

  private final int chunkSize = 10;
  
  /*
  * TODO
  *  여러개의 step 도입 -> 순서보장 체크
  *  스케쥴링 도입
  * */
  

  @Bean
  public Job Job_Record() {
    return jobBuilderFactory.get("Record_refresh_job")
        .start(KeywordRefreshStep())
        .build();
  }

  @Bean
  public Step KeywordRefreshStep() {
    return stepBuilderFactory
        .get("keyword_record_refresh_step")
        .<Keyword, List<KeywordLearningRecord>>chunk(chunkSize)
        .reader(keywordRecordReader())
        .processor(keywordRecordProcessor())
        .writer(keywordRecordWriter())
        .build();
  }

  @Bean
  public ItemWriter<List<KeywordLearningRecord>> keywordRecordWriter() {
    return list -> {
      JpaItemWriter<KeywordLearningRecord> jpaItemWriter = new JpaItemWriter<>();
      jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
      for (List<KeywordLearningRecord> records : list) {
        jpaItemWriter.write(records);
      }
    };
  }

  private ItemProcessor<Keyword, List<KeywordLearningRecord>> keywordRecordProcessor() {
    return keyword -> {
      List<KeywordLearningRecord> newRecordList = new ArrayList<>();
      Map<Customer, List<KeywordLearningRecord>> keywordProgressMap =
          keywordLearningRecordRepository.findAll().stream()
              .collect(Collectors.groupingBy(KeywordLearningRecord::getCustomer));
      List<Customer> customerList = customerRepository.findAll();

      for (Customer customer : customerList) {
        List<KeywordLearningRecord> keywordRecordList = keywordProgressMap.get(customer);
        Map<Keyword, KeywordLearningRecord> keywordLearningRecordMap = new HashMap<>();
        if (keywordRecordList != null) {
          keywordLearningRecordMap =
              keywordRecordList.stream()
                  .collect(Collectors.toMap(KeywordLearningRecord::getKeyword, k -> k));
        }

        KeywordLearningRecord record = keywordLearningRecordMap.get(keyword);
        if (record == null) {
          KeywordLearningRecord newRecord = new KeywordLearningRecord(keyword, customer);
          newRecordList.add(newRecord);
        }
      }
      return newRecordList;
    };
  }



  @Bean
  public ItemReader<Keyword> keywordRecordReader() {
    return new JpaPagingItemReaderBuilder<Keyword>()
        .name("keyword_reader")
        .entityManagerFactory(entityManagerFactory)
        .pageSize(chunkSize)
        .queryString("SELECT k FROM Keyword k ORDER BY k.id")
        .build();
  }
}
