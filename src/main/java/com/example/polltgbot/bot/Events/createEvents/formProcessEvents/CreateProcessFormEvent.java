package com.example.polltgbot.bot.Events.createEvents.formProcessEvents;

import com.example.polltgbot.bot.Events.Event;
import com.example.polltgbot.bot.Events.EventChain;
import com.example.polltgbot.bot.Events.createEvents.formProcessEvents.pollProcessEvents.CreateStartPollEvent;
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

public class CreateProcessFormEvent extends Event<MessageEventType, MessageEventType> {
    @Lazy
    public CreateProcessFormEvent(List<BotApiMethod<?>> emptyList, CreateProcessTextMessageEvent createFormTextMessageProcessEvent, CreateStartPollEvent createFormStartProcessPollEvent, CreateEndFormEvent createFormEndCreationEvent) {
        this.emptyList = emptyList;
        this.createFormTextMessageProcessEvent = createFormTextMessageProcessEvent;
        this.createFormStartProcessPollEvent = createFormStartProcessPollEvent;
        this.createFormEndCreationEvent = createFormEndCreationEvent;
    }

    private final List<BotApiMethod<?>> emptyList;
    @Lazy
    private final CreateProcessTextMessageEvent createFormTextMessageProcessEvent;
    @Lazy
    private final CreateStartPollEvent createFormStartProcessPollEvent;
    @Lazy
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
            System.out.println("no nessage");
            return emptyList;
        }
        Message message = update.getMessage();
        emmitNext = true;

        if (message.isCommand() && message.getText().equals("/create")) {
            chain.setCurrentEvent(createFormEndCreationEvent);
            return emptyList;
        } else if (message.isCommand() && message.getText().equals("/poll")) {
            chain.setCurrentEvent(createFormStartProcessPollEvent);
            return emptyList;
        } else {
            chain.setCurrentEvent(createFormTextMessageProcessEvent);
            return emptyList;
        }

    }
}
