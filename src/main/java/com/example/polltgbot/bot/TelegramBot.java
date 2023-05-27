package com.example.polltgbot.bot;


import com.example.polltgbot.bot.Events.startEvents.StartEvent;
import com.example.polltgbot.data.entities.User;
import com.example.polltgbot.bot.Events.EventChain;
import com.example.polltgbot.services.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.session.TelegramLongPollingSessionBot;

import java.util.*;

@Component
@Slf4j
@AllArgsConstructor
public class TelegramBot extends TelegramLongPollingSessionBot {
    private final UserService userService;
    private final StartEvent startEvent;
    private User getUser(Update update) {
        Long userId;
        String name;
        String surname;
        String tag;
        org.telegram.telegrambots.meta.api.objects.User tgUser;
        if (update.hasMessage()) {
            tgUser = update.getMessage().getFrom();
        } else if (update.hasCallbackQuery()) {
            tgUser = update.getCallbackQuery().getFrom();

        } else {
            throw new IllegalArgumentException();
        }
        userId = tgUser.getId();
        name = tgUser.getFirstName();
        surname = tgUser.getLastName();
        tag = tgUser.getUserName();
        Optional<User> userOpt = userService.getUser(userId);
        return userOpt.orElseGet(() -> userService.createUser(userId, name, surname, tag));
    }

    private final Map<String, EventChain> userEventChainMap = new HashMap<>();
    @Override
    public void onUpdateReceived(Update update, Optional<Session> optional) {
        if (optional.isPresent()) {
            Session session = optional.get();
            User user = getUser(update);
            EventChain eventChain = getEventChain(String.valueOf(user.getUserid()));
            System.out.println(eventChain.getCurrentEvent().getClass().getName());
            for (BotApiMethod<?> botApiMethod : eventChain.execute(update, user, session)) {
                try {
                    execute(botApiMethod);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
        private final BotConfig botConfig;
    private final EventChain getEventChain(String userId){
        EventChain eventChain = userEventChainMap.get(userId);
        if(Objects.isNull(eventChain)){
            eventChain = new EventChain(startEvent);
            userEventChainMap.put(userId, eventChain);
        }
        return eventChain;
    }
     @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public String getBotUsername() {
        return botConfig.getName();
    }
//    private final HandlersContext handlersContext;
//    private final UserService userService;
//    private final BotConfig botConfig;
//    private final TranslationService translationService;
//     @Override
//    public String getBotToken() {
//        return botConfig.getToken();
//    }
//
//    @Override
//    public String getBotUsername() {
//        return botConfig.getName();
//    }
//
//
//    @Override
//    public void onUpdateReceived(Update update, Optional<Session> optional) {
//           if (optional.isPresent()) {
//            Session session = optional.get();
//            try {
//                if (session.getAttribute("stage") == null) session.setAttribute("stage", Stage.START);
//
//                List<BotApiMethod<?>> methods = handleUpdate(update, session);
//                for (BotApiMethod<?> method : methods) {
//                    if (method != null) execute(method);
//                }
//            } catch (TelegramApiException e) {
//                System.out.println("HELLO?");
//                session.setAttribute("stage", Stage.START);
//                throw new RuntimeException(e);
//            }
//        }
//    }
//
//    private User getUser(Update update) {
//        Long userId;
//        String name;
//        String surname;
//        String tag;
//        if (update.hasMessage()) {
//            userId = update.getMessage().getFrom().getId();
//            name = update.getMessage().getFrom().getFirstName();
//            surname = update.getMessage().getFrom().getLastName();
//            tag = update.getMessage().getFrom().getUserName();
//        } else if (update.hasCallbackQuery()) {
//            userId = update.getCallbackQuery().getFrom().getId();
//            name = update.getCallbackQuery().getFrom().getFirstName();
//            surname = update.getCallbackQuery().getFrom().getLastName();
//            tag = update.getCallbackQuery().getFrom().getUserName();
//
//        } else {
//            throw new IllegalArgumentException();
//        }
//        Optional<User> userOpt = userService.getUser(userId);
//        return userOpt.orElseGet(() -> userService.createUser(userId, name, surname, tag));
//    }
//
//    private List<BotApiMethod<?>> handleUpdate(Update update, Session session) throws TelegramApiException {
//        System.out.println("hello");
//        if (!update.hasPoll() && update.hasMessage() && update.getMessage().getFrom().getIsBot()) {
//            return List.of();
//        }
//        System.out.println(2);
//        User user = getUser(update);
//        Stage stage = (Stage) session.getAttribute("stage");
//        if (!session.getAttribute("stage").equals(Stage.START) && update.hasMessage() && update.getMessage().isCommand() &&
//                update.getMessage().getText().equals("/cancel")) {
//            session.setAttribute("stage", Stage.START);
//            return List.of(new SendMessage(String.valueOf(update.getMessage().getChatId()),
//                    translationService.getTranslation(user.getLanguage(), "cancel-msg").getValue()));
//        }
//        System.out.println(update.hasPoll());
//        if (update.hasPoll()) {
//            System.out.println(1);
//            return handlersContext.getMessageHandler(stage, "poll").handle(update, user, session);
//
//        }
//        if (update.hasMessage()) {
//            return handlersContext.getMessageHandler(stage, "message").handle(update, user, session);
//
//        }
//        if (update.hasCallbackQuery()) {
//            return handlersContext.getMessageHandler(stage, "callbackQuery").handle(update, user, session);
//
//        }
//        System.out.println("has poll" + update.hasPoll());
//
//        return List.of();
//    }


}
