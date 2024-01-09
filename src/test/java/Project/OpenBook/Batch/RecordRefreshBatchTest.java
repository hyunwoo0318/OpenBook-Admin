package Project.OpenBook.Batch;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Customer.Repository.CustomerRepository;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.Keyword.Repository.KeywordRepository;
import Project.OpenBook.Domain.Keyword.Service.KeywordService;
import Project.OpenBook.Domain.LearningRecord.KeywordLearningRecord.Domain.KeywordLearningRecord;
import Project.OpenBook.Domain.LearningRecord.KeywordLearningRecord.Repo.KeywordLearningRecordRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@SpringBatchTest
public class RecordRefreshBatchTest {

  @Autowired KeywordRepository keywordRepository;

  @Autowired CustomerRepository customerRepository;

  @Autowired KeywordLearningRecordRepository keywordLearningRecordRepository;

  @Autowired KeywordService keywordService;

  @Autowired private JobLauncherTestUtils jobLauncherTestUtils;

  @Autowired private JobRepositoryTestUtils jobRepositoryTestUtils;

  @BeforeEach
  public void initDB_RemoveExecution() {
    for (int i = 0; i < 100; i++) {
      Keyword keyword = new Keyword("keyword" + i);
      keywordRepository.save(keyword);
    }

    for (int i = 0; i < 10; i++) {
      Customer customer = new Customer();
      customerRepository.save(customer);
    }

    jobRepositoryTestUtils.removeJobExecutions();
  }

  @Test
  @DisplayName("키워드 학습 정도 레코드 생성 작업 테스트")
  public void batchKeywordRecordTest() {
    long start = System.currentTimeMillis();
    JobExecution jobExecution = jobLauncherTestUtils.launchStep("keyword_record_refresh_step");
    long end = System.currentTimeMillis();

    assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

    List<Keyword> keywordList = keywordRepository.findAll();
    List<Customer> customerList = customerRepository.findAll();
    List<KeywordLearningRecord> recordList = keywordLearningRecordRepository.findAll();

    assertThat(recordList.size()).isEqualTo(1000); // 100 * 10
    System.out.println(end - start + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1");
  }

  @Test
  @DisplayName("시간 비교용")
  public void generalKeywordRecordTest() {

    long start = System.currentTimeMillis();
    keywordService.tempLearningRecordInit();
    long end = System.currentTimeMillis();

    System.out.println(end - start + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1");
  }
}
