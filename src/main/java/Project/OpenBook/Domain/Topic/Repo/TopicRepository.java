package Project.OpenBook.Domain.Topic.Repo;

import Project.OpenBook.Domain.Topic.Domain.Topic;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long>, TopicRepositoryCustom {

  public Optional<Topic> findTopicByTitle(String title);
}
