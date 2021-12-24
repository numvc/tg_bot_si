package ccproject.tgbot.sigame.services;


import ccproject.tgbot.sigame.components.AnswerCallBackData;
import ccproject.tgbot.sigame.components.InviteCallBackData;
import ccproject.tgbot.sigame.components.MessageSender;
import ccproject.tgbot.sigame.entities.Match;
import ccproject.tgbot.sigame.entities.User;
import ccproject.tgbot.sigame.repos.MatchRepo;
import ccproject.tgbot.sigame.repos.UserRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;

@Service
public class BotService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MatchRepo matchRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private MatchService matchService;

    @Autowired
    private MessageSender messageSender;

    @Transactional
    public void dispathRequestWithMessage(Update update) throws InterruptedException {
//        checkExpiredInvitations(); добавить проверку просроченных инвайтов
        User newUser, potentialOpponent;
        if (!userService.checkUserExistsById(update)) {
            newUser = userService.mapNewUser(update);
            userRepo.save(newUser);
            messageSender.helloMessage(update);
            return;
        }
        newUser = userRepo.findById(update.getMessage().getChatId()).get();
        String[] command = update.getMessage().getText().split(" ");
        System.out.println(Arrays.toString(command));

        switch (command[0]) {
            case "/rate":
                //проверить не в матче ли
                messageSender.rateMessage(newUser, userRepo.findAll());
                break;
            case "/list":
                //проверить не в матче ли
                messageSender.listMessage(update, newUser, userRepo.findAll());
                break;

            case "/play":
                // проверяем, есть ли такой оппонент
                if (userService.checkUserExistsByUsername(command[1])) {
                    potentialOpponent = userRepo.findByUsername(command[1]).get();
                    if(newUser.getState() > 0) {
                        messageSender.waitForAnswerMessage(newUser, potentialOpponent);
                        return;
                    }
                    if(potentialOpponent.getState() > 0){
                        messageSender.userBusyMessage(newUser);
                        return;
                    }

                    // не играют, можем кинуть приглашение поиграть
                    newUser.setState(1);
                    potentialOpponent.setState(1);
                    userRepo.save(newUser);
                    userRepo.saveAndFlush(potentialOpponent);


                    messageSender.inviteMessage(newUser, potentialOpponent);

                } else
                    // пользователь не существует
                    messageSender.unknownCommandMessage(update);
                break;
            default:
                messageSender.unknownCommandMessage(update);
                break;
        }
    }



    @SneakyThrows
    public void dispatchRequestWithInviteCallback(Update update) {
        //добавить проверку истекших сообщений
        String callback_data = update.getCallbackQuery().getData();
        ObjectMapper mapper = new ObjectMapper();
        InviteCallBackData newInviteCallBackData = mapper.readValue(callback_data, InviteCallBackData.class);

        //можем создавать матч
        User user1 = userRepo.findById(newInviteCallBackData.getReceiverId()).get();
        User user2 = userRepo.findById(newInviteCallBackData.getSenderId()).get();
        if(user1.getState() == 1 && user2.getState() == 1) {
            Match match = matchService.configureMatch(user1, user2);

//            userRepo.save(user1);
//            userRepo.save(user2);

            messageSender.openingMessage(user1, user2);
            matchService.manageMatch(user1);
            matchService.manageMatch(user2);
        } else {
            // кто-то уже в матче
            System.out.println("!кто-то уже в матче. проблема во время создания матча!");
        }
    }

    @SneakyThrows
    public void dispatchRequestWithAnswerCallback(Update update) {
        String callback_data = update.getCallbackQuery().getData();
        ObjectMapper mapper = new ObjectMapper();
        AnswerCallBackData newAnswerCallBackData = mapper.readValue(callback_data, AnswerCallBackData.class);

        User user = userRepo.findById(update.getCallbackQuery().getFrom().getId()).get();
        if(newAnswerCallBackData.isTrueAnswer()){
            user.setC_points(user.getC_points() + 1);
        }
//        if(user.getState() == 3)
//        user.setState(user.getState() + 1);
        matchService.manageMatch(user);

    }

    //допилить просроченные инвайты из БД
//    public void checkExpiredInvitations(){
//        for (Map.Entry<User, User> user : matches.entrySet()) {
//            if( ChronoUnit.MINUTES.between(user.getValue().getLocalDateTimeSend(), LocalDateTime.now()) > 1) {
//                matches.remove(user.getKey());
//                messageSender.invitationExpiredSenderMessage(user.getValue(), user.getKey());
//                messageSender.invitationExpiredReceiverMessage(user.getKey(), user.getValue());
//            }
//        }
//    }


}
