package com.example.polltgbot.bot.Events.createEvents;

import com.example.polltgbot.bot.Events.Event;
import com.example.polltgbot.bot.Events.EventChain;
import com.example.polltgbot.bot.Events.eventTypes.CallbackEventType;
import com.example.polltgbot.bot.Events.eventTypes.MessageEventType;
import com.example.polltgbot.bot.keybords.ReplyKeyboards;
import com.example.polltgbot.data.entities.Form;
import com.example.polltgbot.data.entities.Message;
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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class CreateChooseTimeEvent extends Event<CallbackEventType, MessageEventType> {
    private final TranslationService translationService;
    private final RandomStringGenerator generator;
    private final ReplyKeyboards replyKeyboards;
    private final List<BotApiMethod<?>> emptyList;

    private final CreateRecieveNameEvent createRecieveNameEvent;
    @Override
    public Class<?> getInputEventType() {
        return CallbackEventType.class;
    }

    @Override
    public Class<?> getOutputEventType() {
        return MessageEventType.class;
    }

    @Override
    public List<BotApiMethod<?>> emmit(Update update, User user, Session session, EventChain chain) {
        if(!update.hasCallbackQuery()){
            return emptyList;
        }
        String chatId = String.valueOf(update.getCallbackQuery().getMessage().getChatId());
        CallbackQuery callbackQuery = update.getCallbackQuery();
        if (callbackQuery.getData().contains("time-")) {
            long minutesToExpire = Long.parseLong(callbackQuery.getData().substring(5));
            Form form = new Form(generator.generate(10), user, Timestamp.from(Instant.now()), Timestamp.from(Instant.now().plus(minutesToExpire, ChronoUnit.MINUTES)), null);
            Map<String, Object> eventChainData = chain.getEventChainData();
            eventChainData.put("form", form);
            eventChainData.put("messages", new ArrayList<Message>());

              SendMessage sendMessageWithReplyKeyboard = new SendMessage(chatId,
                      translationService.getTranslation(user.getLanguage(), "time-chosen").getValue());
            sendMessageWithReplyKeyboard.setReplyMarkup(replyKeyboards.createFormKeyboard());
            chain.setCurrentEvent(createRecieveNameEvent);
            setNewEvent = true;
            return List.of(new EditMessageReplyMarkup(String.valueOf(callbackQuery.getMessage().getChatId()), callbackQuery.getMessage().getMessageId(), callbackQuery.getInlineMessageId(), null),
                    sendMessageWithReplyKeyboard);
        }
        return emptyList;
    }
}
