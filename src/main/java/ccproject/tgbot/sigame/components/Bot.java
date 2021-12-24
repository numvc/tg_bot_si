package ccproject.tgbot.sigame.components;

import ccproject.tgbot.sigame.repos.UserRepo;
import ccproject.tgbot.sigame.services.BotService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class Bot extends TelegramLongPollingBot {

    @Autowired
    private BotService botService;

    @Autowired
    private UserRepo userRepo;

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            botService.dispathRequestWithMessage(update);
        } else if (update.hasCallbackQuery()) {
            System.out.println(update.getCallbackQuery().getFrom().getId());
            if(userRepo.findById(update.getCallbackQuery().getFrom().getId()).get().getState() == 1){
                botService.dispatchRequestWithInviteCallback(update);
            } else{
                botService.dispatchRequestWithAnswerCallback(update);
            }




        }
    }

    public void sendMessage(SendMessage sendMessage){
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public String getBotUsername() {
        //@Value("${bot.name}")
        return "SIGame";
    }

    public String getBotToken() {
        //@Value("${bot.token}")
        return "secret";
    }
}