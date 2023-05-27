package com.example.polltgbot.bot.handlers.createHandlers;

import com.example.polltgbot.bot.handlers.UpdateHandler;
import com.example.polltgbot.bot.keybords.ReplyKeyboards;
import com.example.polltgbot.data.entities.Message;
import com.example.polltgbot.data.entities.Form;
import com.example.polltgbot.data.entities.User;
import com.example.polltgbot.services.TranslationService;
import com.example.polltgbot.utils.RandomStringGenerator;
import lombok.AllArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class CreateCallbackQueryHandler implements UpdateHandler {
    private final RandomStringGenerator generator;
    private final TranslationService translationService;
    private final List<BotApiMethod<?>> emptyList;
    private final ReplyKeyboards replyKeyboards;

    @Override
    public List<BotApiMethod<?>> handle(Update update, User user, Session session) {
        String chatId = String.valueOf(update.getCallbackQuery().getMessage().getChatId());
        CallbackQuery callbackQuery = update.getCallbackQuery();
        if (callbackQuery.getData().contains("time-")) {
            long minutesToExpire = Long.parseLong(callbackQuery.getData().substring(5));
            Form form = new Form(generator.generate(10), user, Timestamp.from(Instant.now()), Timestamp.from(Instant.now().plus(minutesToExpire, ChronoUnit.MINUTES)), null);
            session.setAttribute("form-not-created", form);
            session.setAttribute("form-messages-not-created", new ArrayList<Message>());
            String text = String.format(translationService.getTranslation(user.getLanguage(), "time-chosen").getValue(), form.getExpiresAt().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            SendMessage sendMessageWithReplyKeyboard = new SendMessage(chatId, text);
            sendMessageWithReplyKeyboard.setReplyMarkup(replyKeyboards.createFormKeyboard());
            session.setAttribute("waiting-for-name", true);
            return List.of(new EditMessageReplyMarkup(String.valueOf(callbackQuery.getMessage().getChatId()), callbackQuery.getMessage().getMessageId(), callbackQuery.getInlineMessageId(), null),
                    sendMessageWithReplyKeyboard);
        }
        return emptyList;
    }
}
