package Project.OpenBook.Domain.LearningRecord.TimelineLearningRecord.Domain;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Timeline.Domain.Timeline;
import javax.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "timeline_learning_record")
public class TimelineLearningRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timeline_id")
    private Timeline timeline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private Integer answerCount = 0;

    private Integer wrongCount = 0;

    private Integer score = 0;

    public TimelineLearningRecord(Timeline timeline, Customer customer) {
        this.timeline = timeline;
        this.customer = customer;
        this.answerCount = 0;
        this.score = 0;
        this.wrongCount = 0;
    }

    public TimelineLearningRecord(Timeline timeline, Customer customer, Integer answerCount, Integer wrongCount) {
        this.timeline = timeline;
        this.customer = customer;
        this.answerCount = answerCount;
        this.wrongCount = wrongCount;
    }

    public TimelineLearningRecord updateCount(Integer answerCount, Integer wrongCount) {
        this.answerCount += answerCount;
        this.wrongCount += wrongCount;
        return this;
    }

    public TimelineLearningRecord updateScore(Integer wrongCount) {
        Integer ascScore = 10 - wrongCount;
        if(ascScore < 0) ascScore = 0;

        this.score += ascScore;
        if (this.score > 100) {
            this.score = 100;
        }
        return this;
    }

    public void reset() {
        this.answerCount = 0;
        this.wrongCount = 0;
        this.score= 0;
    }

}
