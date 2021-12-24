package ccproject.tgbot.sigame.services;


import ccproject.tgbot.sigame.entities.User;
import ccproject.tgbot.sigame.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    public boolean checkUserExistsById(Update update) {
        Long userChatId = update.getMessage().getChatId();
        return userRepo.findById(userChatId).isPresent();
    }

    public boolean checkUserExistsByUsername(String username) {
        return userRepo.findByUsername(username).isPresent();
    }


    public int getUserState(Long id) {
        return userRepo.findById(id).get().getState();
    }

    public User mapNewUser(Update update){
        User newUser = new User();
        newUser.setId(update.getMessage().getChatId());
        newUser.setUsername(update.getMessage().getFrom().getUserName());
        newUser.setState(0);
        newUser.setPoints(0);
        return newUser;
    }
}
