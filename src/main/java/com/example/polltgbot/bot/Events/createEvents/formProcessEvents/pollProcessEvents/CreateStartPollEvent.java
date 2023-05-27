package com.example.polltgbot.bot.Events.createEvents.formProcessEvents.pollProcessEvents;

import com.example.polltgbot.bot.Events.Event;
import com.example.polltgbot.bot.Events.EventChain;
import com.example.polltgbot.bot.Events.eventTypes.MessageEventType;
import com.example.polltgbot.data.PollDTO;
import com.example.polltgbot.data.entities.User;
import com.example.polltgbot.services.TranslationService;
import lombok.AllArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class CreateStartPollEvent extends Event<MessageEventType, MessageEventType> {
    private final List<BotApiMethod<?>> emptyList;
    private final TranslationService translationService;
    private final CreatePollQuestionEvent createFormQuestionProcessPollEvent;
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
        if(!update.hasMessage() || !update.getMessage().hasText()){
            return emptyList;
        }
        Message message = update.getMessage();
        String chatId = String.valueOf(message.getChatId());
        Map<String, Object> eventChainData = chain.getEventChainData();

        PollDTO pollDTO = new PollDTO(null, new ArrayList<>());
        eventChainData.put("poll", pollDTO);
        setNewEvent = true;
        chain.setCurrentEvent(createFormQuestionProcessPollEvent);
        return List.of(
                new SendMessage(chatId, translationService.getTranslation(user.getLanguage(),
                        "poll-creation-msg").getValue()));
    }
}
