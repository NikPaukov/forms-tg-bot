package com.example.polltgbot.bot.handlers;

import com.example.polltgbot.data.entities.User;
import org.apache.shiro.session.Session;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public interface UpdateHandler {
    public List<BotApiMethod<?>> handle(Update update, User user, Session session);
}
