package com.example.polltgbot.bot.Events.createEvents.formProcessEvents;

import com.example.polltgbot.bot.Events.Event;
import com.example.polltgbot.bot.Events.EventChain;
import com.example.polltgbot.bot.Events.createEvents.formProcessEvents.pollProcessEvents.CreatePollStartEvent;
import com.example.polltgbot.bot.Events.eventTypes.MessageEventType;
import com.example.polltgbot.data.entities.User;
import org.apache.shiro.session.Session;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class CreateHandleFormEvent extends Event<MessageEventType, MessageEventType> {
    @Lazy
    public CreateHandleFormEvent(List<BotApiMethod<?>> emptyList, CreateProcessTextMessageEvent createFormTextMessageProcessEvent, CreatePollStartEvent createPollStartEvent, CreatePollStartEvent createPollStartEvent1, CreateEndFormEvent createFormEndCreationEvent) {
        this.emptyList = emptyList;
        this.createProcessTextMessageEvent = createFormTextMessageProcessEvent;
        this.createPollStartEvent = createPollStartEvent1;
        this.createFormEndCreationEvent = createFormEndCreationEvent;
    }

    private final List<BotApiMethod<?>> emptyList;
    private final CreateProcessTextMessageEvent createProcessTextMessageEvent;
    private final CreatePollStartEvent createPollStartEvent;
    private final CreateEndFormEvent createFormEndCreationEvent;

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
        Message message = update.getMessage();
        emmitNext = true;

        if (message.isCommand() && message.getText().equals("/create")) {
            chain.setCurrentEvent(createFormEndCreationEvent);
            return emptyList;
        } else if (message.isCommand() && message.getText().equals("/poll")) {
            chain.setCurrentEvent(createPollStartEvent);
            return emptyList;
        } else {
            chain.setCurrentEvent(createProcessTextMessageEvent);
            return emptyList;
        }

    }
}
