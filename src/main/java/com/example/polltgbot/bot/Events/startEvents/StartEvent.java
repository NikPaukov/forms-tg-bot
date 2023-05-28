package com.example.polltgbot.bot.Events.startEvents;

import com.example.polltgbot.bot.Events.EventChain;
import com.example.polltgbot.bot.Events.eventTypes.CallbackEventType;
import com.example.polltgbot.bot.Events.eventTypes.MessageEventType;
import com.example.polltgbot.bot.keybords.InlineKeyboards;
import com.example.polltgbot.data.entities.User;
import com.example.polltgbot.bot.Events.Event;
import com.example.polltgbot.services.TranslationService;
import lombok.AllArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@AllArgsConstructor
@Lazy
public class StartEvent extends Event<MessageEventType, CallbackEventType> {
        private final TranslationService translationService;
    private final InlineKeyboards inlineKeyboards;
    private final StartSelectEvent startSelectEvent;

    @Override
    public Class<?> getInputEventType() {
        return MessageEventType.class;
    }

    @Override
    public Class<?> getOutputEventType() {
        return CallbackEventType.class;
    }

    @Override
    public List<BotApiMethod<?>> emmit(Update update, User user, Session session, EventChain chain) {
        System.out.println(1);
        if(!update.hasMessage() || !update.getMessage().hasText()){
            return List.of();
        }
        Message message = update.getMessage();
        String chatId = String.valueOf(message.getChatId());
        if (message.hasText() && message.isCommand() && message.getText().equals("/help"))
        {
            //TODO
            return List.of(new SendMessage(chatId, "HELP MSG"));
        }
        chain.setCurrentEvent(startSelectEvent);
        setNewEvent = true;
            SendMessage sendMessage = new SendMessage(chatId, translationService.getTranslation(user.getLanguage(), "start").getValue());
        sendMessage.setReplyMarkup(inlineKeyboards.getStartKeyboard(user.getLanguage()));
        return List.of(sendMessage);
    }
}
