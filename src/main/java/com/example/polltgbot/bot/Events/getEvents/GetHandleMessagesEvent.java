package com.example.polltgbot.bot.Events.getEvents;

import com.example.polltgbot.bot.Events.Event;
import com.example.polltgbot.bot.Events.EventChain;
import com.example.polltgbot.bot.Events.eventTypes.CallbackEventType;
import com.example.polltgbot.bot.Events.eventTypes.EventType;
import com.example.polltgbot.bot.Events.eventTypes.MessageEventType;
import com.example.polltgbot.bot.Events.eventTypes.UncertainEventType;
import com.example.polltgbot.bot.Events.getEvents.process.GetProcessCallbackEvent;
import com.example.polltgbot.bot.Events.getEvents.process.GetProcessFinishEvent;
import com.example.polltgbot.bot.Events.getEvents.process.GetProcessSendMessagesEvent;
import com.example.polltgbot.data.entities.User;
import org.apache.shiro.session.Session;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@Lazy
public class GetHandleMessagesEvent extends Event<MessageEventType, UncertainEventType> {
    public GetHandleMessagesEvent(List<BotApiMethod<?>> emptyList, @Lazy GetProcessFinishEvent getProcessFinishEvent, @Lazy GetProcessSendMessagesEvent getProcessSendMessagesEvent, @Lazy GetProcessCallbackEvent getProcessCallbackEvent) {
        this.emptyList = emptyList;
        this.getProcessFinishEvent = getProcessFinishEvent;
        this.getProcessSendMessagesEvent = getProcessSendMessagesEvent;
        this.getProcessCallbackEvent = getProcessCallbackEvent;
    }

    @Override
    public Class<?> getInputEventType() {
        return MessageEventType.class;
    }

    private Class<?extends EventType> outputEventType = MessageEventType.class;
    @Override
    public Class<?> getOutputEventType() {
        return outputEventType;
    }
    private final List<BotApiMethod<?>> emptyList;
    private final GetProcessFinishEvent getProcessFinishEvent;
    private final GetProcessSendMessagesEvent getProcessSendMessagesEvent;
    private final GetProcessCallbackEvent getProcessCallbackEvent;
    @Override
    public List<BotApiMethod<?>> emmit(Update update, User user, Session session, EventChain chain) {
        if (update.hasCallbackQuery()){
            emmitNext = true;
            outputEventType = CallbackEventType.class;
            chain.setCurrentEvent(getProcessCallbackEvent);
            return emptyList;
        }
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return emptyList;
        }
        outputEventType = MessageEventType.class;
        emmitNext = true;
        System.out.println(update.getMessage().getText());
        if (update.getMessage().getText().equals("/next")) {
            chain.setCurrentEvent(getProcessSendMessagesEvent);
            return emptyList;}
        if (update.getMessage().getText().equals("/finish")) {
            chain.setCurrentEvent(getProcessFinishEvent);
            return emptyList;}
        if (update.getMessage().getText().equals("/cancel")) {
            chain.setCurrentEvent(getProcessFinishEvent);
            return emptyList;}
        return emptyList;
}

}
