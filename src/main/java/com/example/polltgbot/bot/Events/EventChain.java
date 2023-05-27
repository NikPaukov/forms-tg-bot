package com.example.polltgbot.bot.Events;

import com.example.polltgbot.data.entities.User;
import jakarta.el.ELContextEvent;
import lombok.AllArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EventChain {
    private Event<?, ?> currentEvent;

    public EventChain(Event<?, ?> currentEvent) {
        this.currentEvent = currentEvent;
    }

    public Event<?, ?> getCurrentEvent() {
        return currentEvent;
    }

    private final LinkedList<Event<?, ?>> eventHistory = new LinkedList<>();
    private final Map<String, Object> eventChainData = new HashMap<>();

    public void setCurrentEvent(Event<?, ?> newEvent) {
        if (currentEvent.getOutputEventType().getName().equals(newEvent.getInputEventType().getName())) {
            eventHistory.add(currentEvent);
            currentEvent.setSetNewEvent(true);
            currentEvent = newEvent;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public Map<String, Object> getEventChainData() {
        return eventChainData;
    }


    public List<Event<?, ?>> getEventHistory() {
        return Collections.unmodifiableList(eventHistory);
    }


    public List<BotApiMethod<?>> execute(Update update, User user, Session session) {
        if (currentEvent != null) {
            try {
                List<BotApiMethod<?>> res = currentEvent.emmit(update, user, session, this);

                if(eventHistory.getLast().isSetNewEvent() && eventHistory.getLast().isEmmitNext()) {
                    System.out.println("emmit next" + currentEvent.getClass().getName());
                    List<BotApiMethod<?>> res2 = currentEvent.emmit(update, user, session, this);
                    res = Stream.of(res, res2)
                            .flatMap(Collection::stream).toList();
                }
                return res;

            } catch (Exception e) {
                e.printStackTrace();
                if (eventHistory.size() != 0)
                    currentEvent = eventHistory.getLast();
            }
        }
        return List.of();
    }

}

