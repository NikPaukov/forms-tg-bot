package com.example.polltgbot.bot.Events.getEvents.process;

import com.example.polltgbot.bot.Events.Event;
import com.example.polltgbot.bot.Events.EventChain;
import com.example.polltgbot.bot.Events.eventTypes.MessageEventType;
import com.example.polltgbot.bot.Events.startEvents.StartEvent;
import com.example.polltgbot.data.entities.Answer;
import com.example.polltgbot.data.entities.User;
import com.example.polltgbot.services.AnswerService;
import com.example.polltgbot.services.TranslationService;
import lombok.AllArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
@Lazy
public class GetProcessFinishEvent extends Event<MessageEventType, MessageEventType> {
    @Override
    public Class<?> getInputEventType() {
        return MessageEventType.class;
    }

    @Override
    public Class<?> getOutputEventType() {
        return MessageEventType.class;
    }
private final List<BotApiMethod<?>> emptyList;
    @Lazy
    private final StartEvent startEvent;
private final AnswerService answerService;
TranslationService translationService;
    @Override
    public List<BotApiMethod<?>> emmit(Update update, User user, Session session, EventChain chain) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return emptyList;
        }
        String chatId = String.valueOf(update.getMessage().getChatId());
        Message message = update.getMessage();
        Map<String, Object> eventChainData = chain.getEventChainData();
        List<Answer> answers = (List<Answer>) eventChainData.get("answers");
        answers.forEach(answerService::createAnswer);
        setNewEvent=true;
        chain.setCurrentEvent(startEvent);
        return List.of(new SendMessage(chatId, translationService.getTranslation(user.getLanguage(), "finish-msg").getValue()));
    }
}
