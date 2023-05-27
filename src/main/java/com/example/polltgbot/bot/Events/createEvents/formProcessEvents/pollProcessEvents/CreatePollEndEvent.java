package com.example.polltgbot.bot.Events.createEvents.formProcessEvents.pollProcessEvents;

import com.example.polltgbot.bot.Events.Event;
import com.example.polltgbot.bot.Events.EventChain;
import com.example.polltgbot.bot.Events.createEvents.formProcessEvents.CreateProcessFormEvent;
import com.example.polltgbot.bot.Events.eventTypes.MessageEventType;
import com.example.polltgbot.data.PollDTO;
import com.example.polltgbot.data.entities.Form;
import com.example.polltgbot.data.entities.User;
import com.example.polltgbot.data.enums.MessageTypeEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class CreatePollEndEvent extends Event<MessageEventType, MessageEventType> {
    private final List<BotApiMethod<?>> emptyList;
    private final ObjectMapper objectMapper;
    private final CreateProcessFormEvent createFormProcessEvent;

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
        String chatId = String.valueOf(update.getMessage().getChatId());
        Map<String, Object> eventChainData = chain.getEventChainData();
        PollDTO pollDTO = (PollDTO) eventChainData.get("poll");
        Integer sendOrder = (Integer) eventChainData.getOrDefault("sendOrder", 0);
        if (message.getText().equals("/poll_cancel")) {
            eventChainData.remove("poll");
            setNewEvent = true;
            chain.setCurrentEvent(createFormProcessEvent);
            return List.of(new SendMessage(chatId, "Опитування відмінено!"));

        }
        else if (message.getText().equals("/poll_end")) {
            Form form = (Form) eventChainData.get("form");
            try {
                String res = objectMapper.writeValueAsString((PollDTO) pollDTO);
                com.example.polltgbot.data.entities.Message messageEntity = new com.example.polltgbot.data.entities.Message(
                        form, sendOrder, MessageTypeEnum.POLL, res);
                eventChainData.remove("poll");
                eventChainData.put("sendOrder", sendOrder + 1);
                ((List<com.example.polltgbot.data.entities.Message>) eventChainData.get("messages"))
                        .add(messageEntity);
                setNewEvent = true;
                chain.setCurrentEvent(createFormProcessEvent);
                //Todo
                return List.of(new SendMessage(chatId, "Опитування створено!"));
            } catch (JsonProcessingException e) {
                //TODO
                return List.of(new SendMessage(chatId, "ПОМИЛКА"));
            }
        }
        return emptyList;
    }
}
