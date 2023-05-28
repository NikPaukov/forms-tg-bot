package com.example.polltgbot.bot.Events.createEvents;

import com.example.polltgbot.bot.Events.Event;
import com.example.polltgbot.bot.Events.EventChain;
import com.example.polltgbot.bot.Events.createEvents.formProcessEvents.CreateHandleFormEvent;
import com.example.polltgbot.bot.Events.eventTypes.MessageEventType;
import com.example.polltgbot.data.entities.Form;
import com.example.polltgbot.data.entities.User;
import com.example.polltgbot.services.TranslationService;
import lombok.AllArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class CreateRecieveNameEvent extends Event<MessageEventType, MessageEventType> {
    private final List<BotApiMethod<?>> emptyList;
    TranslationService translationService;

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
        String chatId = String.valueOf(message.getChatId());
        Map<String, Object> eventChainData = chain.getEventChainData();
        Form form = (Form) eventChainData.get("form");
        form.setName(message.getText());
        chain.setCurrentEvent(createHandleFormEvent);
        String text = String.format(translationService.getTranslation(user.getLanguage(), "name-chosen").getValue(),
                form.getName(),
                form.getExpiresAt().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        setNewEvent = true;
        return List.of(new SendMessage(chatId,text));

    }
}
