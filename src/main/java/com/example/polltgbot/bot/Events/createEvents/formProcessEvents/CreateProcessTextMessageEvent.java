package com.example.polltgbot.bot.Events.createEvents.formProcessEvents;

import com.example.polltgbot.bot.Events.Event;
import com.example.polltgbot.bot.Events.EventChain;
import com.example.polltgbot.bot.Events.eventTypes.MessageEventType;
import com.example.polltgbot.data.entities.Form;
import com.example.polltgbot.data.entities.User;
import com.example.polltgbot.data.enums.MessageTypeEnum;
import org.apache.shiro.session.Session;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Map;

@Component
public class CreateProcessTextMessageEvent extends Event<MessageEventType, MessageEventType> {
    @Lazy
    public CreateProcessTextMessageEvent(List<BotApiMethod<?>> emptyList, CreateHandleFormEvent createHandleFormEvent) {
        this.emptyList = emptyList;
        this.createHandleFormEvent = createHandleFormEvent;
    }

    private final List<BotApiMethod<?>> emptyList;

    @Lazy
    private final CreateHandleFormEvent createHandleFormEvent;
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
        Map<String, Object> eventChainData = chain.getEventChainData();
        Integer sendOrder = (Integer) eventChainData.getOrDefault("sendOrder", 0);

        Form form = (Form) eventChainData.get("form");

        com.example.polltgbot.data.entities.Message messageDTO = new com.example.polltgbot.data.entities.Message(
                form, sendOrder, MessageTypeEnum.TEXT, message.getText()
        );
        ((List<com.example.polltgbot.data.entities.Message>) eventChainData.get("messages"))
                .add(messageDTO);
        chain.setCurrentEvent(createHandleFormEvent);
        setNewEvent = true;
        eventChainData.put("sendOrder", sendOrder+1);
        return emptyList;
    }
}
