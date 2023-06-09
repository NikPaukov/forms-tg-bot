package com.example.polltgbot.bot.Events.createEvents.formProcessEvents;

import com.example.polltgbot.bot.Events.Event;
import com.example.polltgbot.bot.Events.EventChain;
import com.example.polltgbot.bot.Events.eventTypes.MessageEventType;
import com.example.polltgbot.bot.Events.startEvents.StartEvent;
import com.example.polltgbot.data.entities.Form;
import com.example.polltgbot.data.entities.User;
import com.example.polltgbot.services.MessageService;
import com.example.polltgbot.services.PollService;
import com.example.polltgbot.services.TranslationService;
import lombok.AllArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class CreateEndFormEvent extends Event<MessageEventType, MessageEventType> {
    private final List<BotApiMethod<?>> emptyList;
    private final PollService formService;
    private final MessageService messageService;
        private final StartEvent startEvent;
    private final TranslationService translationService;
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
        String chatId = String.valueOf(update.getMessage().getChatId());
        Map<String, Object> eventChainData = chain.getEventChainData();

        Form form = (Form) eventChainData.get("form");
        List<com.example.polltgbot.data.entities.Message> messages =
                (List<com.example.polltgbot.data.entities.Message>) eventChainData.get("messages");
        formService.createForm(form);
        messageService.createMessages(messages);
        //TODO
        setNewEvent=true;
        chain.setCurrentEvent(startEvent);
        SendMessage sendMessage = new SendMessage(chatId, String.format(translationService.getTranslation(user.getLanguage(),
                "form-created-msg").getValue(),
                form.getName(),
                form.getCode()));
        sendMessage.enableMarkdown(true);
        return List.of(sendMessage);
    }
}
