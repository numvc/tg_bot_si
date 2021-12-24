package ccproject.tgbot.sigame.services;

import ccproject.tgbot.sigame.components.MessageSender;
import ccproject.tgbot.sigame.entities.Match;
import ccproject.tgbot.sigame.entities.Question;
import ccproject.tgbot.sigame.entities.User;
import ccproject.tgbot.sigame.repos.MatchRepo;
import ccproject.tgbot.sigame.repos.QuestionRepo;
import ccproject.tgbot.sigame.repos.UserRepo;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class MatchService {
    @Autowired
    private QuestionRepo questionRepo;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private UserRepo userRepo;


    @Autowired
    private MatchRepo matchRepo;

    public Match configureMatch(User player1, User player2) {
        Match newMatch = new Match();
        newMatch.setPlayer_1(player1);
        newMatch.setPlayer_2(player2);

        newMatch.setQuestions(getRandomQuestion(3));
        player1.setCur_match(newMatch);
        player2.setCur_match(newMatch);

        matchRepo.save(newMatch);

        return newMatch;
    }

    public void manageMatch(User user) {
        if (user.getState() == 4) {
            finishMatch(user);
        } else {
            user.setState(user.getState() + 1);
            sendQuestion(user.getState() - 2, user.getCur_match().getQuestions(), user);
            userRepo.save(user);
        }
    }

    @SneakyThrows
    public void finishMatch(User user) {
        // проверяем, что оба игрока закончили матч
        User opponent = user.getCur_match().getPlayer_1();

        if(opponent.getUsername().equals(user.getUsername())) {
            opponent = user.getCur_match().getPlayer_2();
        }



        //отправить сообщение о финише, набранные очки и текущий рейт
        if(opponent.getState() == 4) {
            user.setPoints(user.getPoints() + (float) user.getC_points() / 3);
            opponent.setPoints(opponent.getPoints() + (float) opponent.getC_points() / 3);
            userRepo.save(user);
            userRepo.save(opponent);

            messageSender.closingMessage(user, opponent);
//            Thread.sleep(500);
//            messageSender.rateMessage(user, userRepo.findAll());
            Thread.sleep(500);
            messageSender.closingMessage(opponent, user);
//            Thread.sleep(500);
//            messageSender.rateMessage(opponent, userRepo.findAll());
//            Thread.sleep(500);

            user.setState(0);
            user.setC_points(0);

            opponent.setState(0);
            opponent.setC_points(0);
            userRepo.save(user);
            userRepo.save(opponent);
        }
    }

    private List<Question> getRandomQuestion(int count) {
        List<Question> questions = new LinkedList<>();
        for (int i = 1; i <= count; i++) {
            questions.add(questionRepo.findById(i).get());
        }
        System.out.println(questions.toString());
        return questions;
    }

    private void sendQuestion(int questionNumber, List<Question> questions, User user) {
        System.out.println(questions.toString());
        messageSender.sendQuestion(user, questions.get(questionNumber));
    }

}
