package com.example.polltgbot.bot.Events.getEvents.process;

import com.example.polltgbot.bot.Events.Event;
import com.example.polltgbot.bot.Events.EventChain;
import com.example.polltgbot.bot.Events.eventTypes.MessageEventType;
import com.example.polltgbot.bot.Events.getEvents.GetHandleMessagesEvent;
import com.example.polltgbot.bot.keybords.InlineKeyboards;
import com.example.polltgbot.data.PollDTO;
import com.example.polltgbot.data.entities.Form;
import com.example.polltgbot.data.entities.User;
import com.example.polltgbot.data.enums.MessageTypeEnum;
import com.example.polltgbot.services.MessageService;
import com.example.polltgbot.services.PollService;
import com.example.polltgbot.services.TranslationService;
import com.example.polltgbot.utils.Serializers.Deserializers.MessageDeserializer;
import com.example.polltgbot.utils.Serializers.Deserializers.MessageDeserializersHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class GetProcessSendMessagesEvent extends Event<MessageEventType, MessageEventType> {
    @Override
    public Class<?> getInputEventType() {
        return MessageEventType.class;
    }

    @Override
    public Class<?> getOutputEventType() {
        return MessageEventType.class;
    }

    private final List<BotApiMethod<?>> emptyList;
    private final TranslationService translationService;
    private final MessageService messageService;
    private final MessageDeserializersHandler messageDeserializersHandler;
    private final InlineKeyboards inlineKeyboards;
    private final PollService formService;
    private final GetHandleMessagesEvent getHandleMessagesEvent;

    @Override
    public List<BotApiMethod<?>> emmit(Update update, User user, Session session,EventChain chain) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return emptyList;
        }
        setNewEvent=true;
        chain.setCurrentEvent(getHandleMessagesEvent);

        String chatId = String.valueOf(update.getMessage().getChatId());
        Map<String, Object> eventChainData = chain.getEventChainData();
        eventChainData.putIfAbsent("passed",0);
        int index = (int) eventChainData.get("passed");
        List<BotApiMethod<?>> res = new ArrayList<>();

        Form form = formService.getFormByCode(String.valueOf(eventChainData.get("code")));
        List<com.example.polltgbot.data.entities.Message> messages = messageService.findAllByPoll(form);
        List<com.example.polltgbot.data.entities.Message> filteredMessages = messages.stream().skip(index).limit(3).toList();
        eventChainData.put("passed", index + filteredMessages.size());
        if (filteredMessages.size() == 0) {
            res.add(new SendMessage(chatId, translationService.getTranslation(user.getLanguage(), "form-done").getValue()));
            return res;
        }
        for (com.example.polltgbot.data.entities.Message message : filteredMessages) {
            MessageDeserializer messageDeserializer = messageDeserializersHandler.getMessageSerializer(message.getMessageType());
            Object answer;
            try {
                answer = messageDeserializer.deserialize(message.getData());
            } catch (JsonProcessingException e) {
                return List.of(new SendMessage(chatId, "СТАЛАСЯ ПОМИЛКА"));
            }
            if (message.getMessageType() == MessageTypeEnum.TEXT) {
                res.add(new SendMessage(chatId, (String) answer));
            }
            if (message.getMessageType() == MessageTypeEnum.POLL) {

                PollDTO regularPollDTO = (PollDTO) answer;
                SendMessage sendMessage = new SendMessage(chatId, regularPollDTO.getQuestion());
                sendMessage.setReplyMarkup(inlineKeyboards.getOptionsKeyboard(regularPollDTO.getOptions(), message.getId()));
                res.add(sendMessage);
            }
        }
        if (filteredMessages.size() < 3) {
            res.add(new SendMessage(chatId, translationService.getTranslation(user.getLanguage(), "form-done").getValue()));
        }
        return res;
    }
}

