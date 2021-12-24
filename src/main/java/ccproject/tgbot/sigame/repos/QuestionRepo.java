package ccproject.tgbot.sigame.repos;

import ccproject.tgbot.sigame.entities.Question;
import ccproject.tgbot.sigame.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionRepo extends JpaRepository<Question, String> {
    Optional<Question> findById(int id);
}
