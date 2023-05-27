package com.example.polltgbot.bot.handlers.infoHandlers;

import com.example.polltgbot.bot.handlers.UpdateHandler;
import com.example.polltgbot.data.entities.User;
import lombok.AllArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@AllArgsConstructor
public class InfoMessageHandler implements UpdateHandler {
    private final List<BotApiMethod<?>> emptyList;
    @Override
    public List<BotApiMethod<?>> handle(Update update, User user, Session session) {

        return emptyList;
    }
}
