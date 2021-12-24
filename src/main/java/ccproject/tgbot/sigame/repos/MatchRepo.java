package ccproject.tgbot.sigame.repos;

import ccproject.tgbot.sigame.entities.Match;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepo extends JpaRepository<Match, String> {
}
