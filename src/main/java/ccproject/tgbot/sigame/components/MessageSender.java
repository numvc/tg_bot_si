package ccproject.tgbot.sigame.components;

import ccproject.tgbot.sigame.entities.Question;
import ccproject.tgbot.sigame.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Component
public class MessageSender {

    @Autowired
    private Bot bot;

    public void helloMessage(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
        sendMessage.setText(update.getMessage().getFrom().getUserName() + ", дядь, соси хуй, эта игра не для долбаебов, пока не поздно закрывай.");
        bot.sendMessage(sendMessage);
    }

    public void unknownCommandMessage(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
        sendMessage.setText("Неизвестная команда");
        bot.sendMessage(sendMessage);
    }

    public void rateMessage(User user, List<User> users) {
        users.sort(Comparator.comparingDouble(User::getPoints).reversed());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(user.getId()));
        int userRate = users.indexOf(user) + 1;
        sendMessage.setText("У тебя " + String.valueOf(user.getPoints()) + " очков. \nМесто " + String.valueOf(userRate) + " из "
                + users.size());

        bot.sendMessage(sendMessage);
    }

    public void listMessage(Update update, User user, List<User> users) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
        sendMessage.setText("Выбирай оппонента:\n" + getOpponentList(users, user) +
                "Пиши /play username и поехали");
        bot.sendMessage(sendMessage);
    }

    private String getOpponentList(List<User> users, User curUser) {
        String opponentList = "id                     Username                Points\n";
        for (User user : users) {
            if (!curUser.equals(user))
                opponentList += user.getId() + "    " + user.getUsername() + "                " + user.getPoints() + "\n";
        }
        opponentList += "\n";
        return opponentList;
    }

    public void inviteMessage(User sender, User receiver) {
        SendMessage sendMessage = new SendMessage();


        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        rowInline.add(createInviteButton("Go", getCallBackInviteData(sender, receiver, true)));
        rowInline.add(createInviteButton("Ne go", getCallBackInviteData(sender, receiver, false)));
        // Set the keyboard to the markup
        rowsInline.add(rowInline);
        // Add it to the message
        markupInline.setKeyboard(rowsInline);

        sendMessage.setChatId(String.valueOf(receiver.getId()));
        sendMessage.setText(sender.getUsername() + " кинул тебе вызов. Погнали?");
        sendMessage.setReplyMarkup(markupInline);
        bot.sendMessage(sendMessage);

    }

    public InlineKeyboardButton createInviteButton(String message, InviteCallBackData callback) {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setCallbackData(callback.toJson());
        inlineKeyboardButton.setText(message);
        return inlineKeyboardButton;
    }

    public InviteCallBackData getCallBackInviteData(User sender, User receiver, boolean play) {
        InviteCallBackData inviteCallBackData = new InviteCallBackData();
        inviteCallBackData.setPlay(play);
        inviteCallBackData.setSenderId(sender.getId());
        inviteCallBackData.setReceiverId(receiver.getId());
        return inviteCallBackData;
    }

    public void rejectInviteMessage(User inviteSender, User opponent) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(inviteSender.getId()));
        sendMessage.setText(opponent.getUsername() + " пока не готов показать себя...");
        bot.sendMessage(sendMessage);
    }

    public void wrongMessage(User user) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(user.getId()));
        sendMessage.setText("Что-то пошло не так");
        bot.sendMessage(sendMessage);
    }

    public void openingMessage(User user1, User user2) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(user1.getId()));
        sendMessage.setText("Автор игры - Владимир Хиль");
        bot.sendMessage(sendMessage);

        sendMessage.setChatId(String.valueOf(user2.getId()));
        sendMessage.setText("Автор игры - Владимир Хиль");
        bot.sendMessage(sendMessage);
    }

    public void waitForAnswerMessage(User userDontAnswer, User userWait) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(userDontAnswer.getId()));
        sendMessage.setText("Ты еще не разобрался с " + userWait.getUsername());
        bot.sendMessage(sendMessage);
    }

    public void userBusyMessage(User user) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(user.getId()));
        sendMessage.setText("Этот игрок сейчас в матче");
        bot.sendMessage(sendMessage);
    }

    public void invitationExpiredSenderMessage(User user, User opponent) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(user.getId()));
        sendMessage.setText("Приглашение для " + opponent.getUsername() + " истекло");
        bot.sendMessage(sendMessage);
    }

    public void invitationExpiredReceiverMessage(User user, User sender) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(user.getId()));
        sendMessage.setText("Приглашение от " + sender.getUsername() + " истекло");
        bot.sendMessage(sendMessage);
    }

    public void closingMessage(User user, User opponent) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(user.getId()));
        sendMessage.setText("Конец. Ты набрал " + user.getC_points() + " из 3 \n" +
                opponent.getUsername() + " набрал " + opponent.getC_points());
        bot.sendMessage(sendMessage);
    }

    public void sendQuestion(User user, Question question) {
        SendMessage sendMessage = new SendMessage();

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        rowInline.add(createAnswerButton(question.getAnswer1(), getCallBackAnswerData(false)));
        rowInline.add(createAnswerButton(question.getAnswer2(), getCallBackAnswerData(false)));
        rowInline.add(createAnswerButton(question.getAnswer3(), getCallBackAnswerData(false)));
        rowInline.add(createAnswerButton(question.getAnswerTrue(), getCallBackAnswerData(true)));
        // Set the keyboard to the markup
        rowsInline.add(rowInline);
        // Add it to the message
        markupInline.setKeyboard(rowsInline);

        sendMessage.setChatId(String.valueOf(user.getId()));
        sendMessage.setText(question.getQuestion());
        sendMessage.setReplyMarkup(markupInline);
        bot.sendMessage(sendMessage);
    }

    private AnswerCallBackData getCallBackAnswerData(Boolean isTrue) {
        AnswerCallBackData answerCallBackData = new AnswerCallBackData();
        answerCallBackData.setTrueAnswer(isTrue);

        return answerCallBackData;

    }

    private InlineKeyboardButton createAnswerButton(String message, AnswerCallBackData callback) {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setCallbackData(callback.toJson());
        inlineKeyboardButton.setText(message);
        return inlineKeyboardButton;
    }


}
