package com.example.polltgbot.bot.handlers.startHandlers;

import com.example.polltgbot.bot.handlers.UpdateHandler;
import com.example.polltgbot.bot.keybords.InlineKeyboards;
import com.example.polltgbot.data.entities.User;
import com.example.polltgbot.services.TranslationService;
import lombok.AllArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@AllArgsConstructor
public class StartMessageHandler implements UpdateHandler {
    private final TranslationService translationService;
    private final InlineKeyboards inlineKeyboards;

    public List<BotApiMethod<?>> handle(Update update, User user, Session session) {
        Message message = update.getMessage();
        String chatId = String.valueOf(message.getChatId());
//        if (message.isCommand()) {
//            //TODO додати help
//
//            if (message.getText().equals("/start")) {
                SendMessage sendMessage = new SendMessage(chatId, translationService.getTranslation(user.getLanguage(), "start").getValue());
                sendMessage.setReplyMarkup(inlineKeyboards.getStartKeyboard(user.getLanguage()));
                return List.of(sendMessage);
//            }
//        }
//        return List.of(new SendMessage(chatId, "sosi hui"));
    }
}
