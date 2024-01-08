package Project.OpenBook.Domain.LearningRecord.RoundLearningRecord;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Round.Domain.Round;
import javax.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "round_learning_record")
public class RoundLearningRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id")
    private Round round;

    @ManyToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private Integer score = 0;

    public RoundLearningRecord(Round round, Customer customer) {
        this.round = round;
        this.customer = customer;
    }

    public void updateScore(Integer score) {
        this.score += score;
    }

    public void clearScore() {
        this.score = 0;
    }

    public void reset() {
        this.score = 0;
    }
}

