package ccproject.tgbot.sigame.repos;

import ccproject.tgbot.sigame.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User, String> {
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    List<User> findAll();
}