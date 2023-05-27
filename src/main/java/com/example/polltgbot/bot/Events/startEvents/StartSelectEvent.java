package com.example.polltgbot.bot.Events.startEvents;

import com.example.polltgbot.bot.Events.EventChain;
import com.example.polltgbot.bot.Events.createEvents.CreateTimeChooseEvent;
import com.example.polltgbot.bot.Events.eventTypes.CallbackEventType;
import com.example.polltgbot.bot.Events.eventTypes.MessageEventType;
import com.example.polltgbot.bot.Events.eventTypes.UncertainEventType;
import com.example.polltgbot.bot.Events.getEvents.GetCodeReceiveEvent;
import com.example.polltgbot.bot.keybords.InlineKeyboards;
import com.example.polltgbot.data.entities.User;
import com.example.polltgbot.bot.Events.Event;
import com.example.polltgbot.services.TranslationService;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
@Component
public class StartSelectEvent extends Event<CallbackEventType, UncertainEventType> {
   private final TranslationService translationService;

    public StartSelectEvent(TranslationService translationService, List<BotApiMethod<?>> emptyList, InlineKeyboards inlineKeyboards, CreateTimeChooseEvent createTimeChooseEvent, GetCodeReceiveEvent getCodeReceiveEvent) {
        this.translationService = translationService;
        this.emptyList = emptyList;
        this.inlineKeyboards = inlineKeyboards;
        this.createTimeChooseEvent = createTimeChooseEvent;
        this.getCodeReceiveEvent = getCodeReceiveEvent;
    }

    private final List<BotApiMethod<?>> emptyList;
   private final InlineKeyboards inlineKeyboards;
   private final CreateTimeChooseEvent createTimeChooseEvent;
   private final GetCodeReceiveEvent getCodeReceiveEvent;
    @Override
    public Class<?> getInputEventType() {
        return CallbackEventType.class;
    }
    private Class<?> outputEventType;

    @Override
    public Class<?> getOutputEventType() {
        return outputEventType;
    }


    @Override
    public List<BotApiMethod<?>> emmit(Update update, User user, Session session, EventChain chain) {
        if(!update.hasCallbackQuery()){
            return emptyList;
        }
        String chatId = String.valueOf(update.getCallbackQuery().getMessage().getChatId());
        CallbackQuery callbackQuery = update.getCallbackQuery();
        if(callbackQuery.getData().equals("create")){
            outputEventType = CallbackEventType.class;
            chain.setCurrentEvent(createTimeChooseEvent);
            SendMessage message = new SendMessage(chatId, translationService.getTranslation(user.getLanguage(), "create-poll-msg").getValue());
            message.setReplyMarkup(inlineKeyboards.getTimeChoseKeyboard(user.getLanguage()));
            return List.of(message);

        }if(callbackQuery.getData().equals("create")){
            outputEventType = MessageEventType.class;
            chain.setCurrentEvent(getCodeReceiveEvent);
            return List.of(new SendMessage(chatId, translationService.getTranslation(user.getLanguage(), "request-code").getValue()));

        }
        return emptyList;
    }
}
