package com.example.polltgbot.bot.Events;

import com.example.polltgbot.bot.Events.eventTypes.EventType;
import com.example.polltgbot.data.entities.User;
import org.apache.shiro.session.Session;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public abstract class Event<T extends EventType, R extends EventType> {
    public abstract Class<?> getInputEventType();
    public abstract Class<?> getOutputEventType();
    public abstract List<BotApiMethod<?>> emmit(Update update, User user, Session session, EventChain chain);

    public boolean isSetNewEvent() {
        return setNewEvent;
    }

    public void setSetNewEvent(boolean setNewEvent) {
        this.setNewEvent = setNewEvent;
    }
    public boolean isEmmitNext() {
        return emmitNext;
    }

    public void setEmmitNext(boolean emmitNext) {
        this.emmitNext = emmitNext;
    }
    protected boolean setNewEvent = false;



    protected boolean emmitNext = false;


}
