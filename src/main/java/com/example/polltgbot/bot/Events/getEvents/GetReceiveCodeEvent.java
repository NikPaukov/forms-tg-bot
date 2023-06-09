package com.example.polltgbot.bot.Events.getEvents;

import com.example.polltgbot.bot.Events.Event;
import com.example.polltgbot.bot.Events.EventChain;
import com.example.polltgbot.bot.Events.eventTypes.MessageEventType;
import com.example.polltgbot.bot.Events.getEvents.process.GetProcessSendMessagesEvent;
import com.example.polltgbot.bot.keybords.ReplyKeyboards;
import com.example.polltgbot.data.entities.Answer;
import com.example.polltgbot.data.entities.Form;
import com.example.polltgbot.data.entities.User;
import com.example.polltgbot.services.AnswerService;
import com.example.polltgbot.services.MessageService;
import com.example.polltgbot.services.PollService;
import com.example.polltgbot.services.TranslationService;
import lombok.AllArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class GetReceiveCodeEvent extends Event<MessageEventType, MessageEventType> {
    private final TranslationService translationService;
    private final List<BotApiMethod<?>> emptyList;
    private final PollService formService;
    private final ReplyKeyboards replyKeyboards;
    private final GetProcessSendMessagesEvent getProcessSendMessagesEvent;

    @Override
    public Class<?> getInputEventType() {
        return MessageEventType.class;
    }

    @Override
    public Class<?> getOutputEventType() {
        return MessageEventType.class;
    }

    @Override
    public List<BotApiMethod<?>> emmit(Update update, User user, Session session, EventChain chain) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return emptyList;
        }
        String chatId = String.valueOf(update.getMessage().getChatId());
        Message message = update.getMessage();
        Map<String, Object> eventChainData = chain.getEventChainData();
        Form form;
        try {
            form = formService.getFormByCode(message.getText());
        } catch (IllegalArgumentException e) {
            return List.of(new SendMessage(chatId, translationService.getTranslation(user.getLanguage(), "wrong-code").getValue()));
        }
        //TODO TODO
        if (false) {
            //TODO
            return List.of(new SendMessage(chatId, translationService.getTranslation(user.getLanguage(),
                    "form-passed-err").getValue()));
        }
        if (form.getExpiresAt().before(Timestamp.from(Instant.now()))) {
            //TODO
            return List.of(new SendMessage(chatId, translationService.getTranslation(
                    user.getLanguage(),"form-expired-err"
            ).getValue()));
        }

        eventChainData.put("currentMessage", 0);
        eventChainData.put("answers", new ArrayList<Answer>());
        eventChainData.put("code", update.getMessage().getText());
        setNewEvent=true;
        emmitNext=true;
        chain.setCurrentEvent(getProcessSendMessagesEvent);
        SendMessage sendMessage = new SendMessage(chatId,
                String.format(translationService.getTranslation(user.getLanguage(), "form-get-msg").getValue(),
                        form.getName())
        );
        sendMessage.setReplyMarkup(replyKeyboards.getFormKeyboard());
        return List.of(sendMessage);
       }


}
