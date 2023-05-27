package com.example.polltgbot.bot.handlers.infoHandlers;

import com.example.polltgbot.bot.handlers.UpdateHandler;
import com.example.polltgbot.data.entities.Form;
import com.example.polltgbot.data.entities.User;
import com.example.polltgbot.services.PollService;
import lombok.AllArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
public class InfoCallbackQueryHandler implements UpdateHandler {
    private final List<BotApiMethod<?>> emptyList;
    private final PollService formService;
    @Override
    public List<BotApiMethod<?>> handle(Update update, User user, Session session) {
        if(!Objects.isNull(session.getAttribute("show-forms-list"))){

        }
        return emptyList;
    }
    /*
    Функціонал:
    Завантажити результати
    Видалити Форму
    Продлити Форму
     */
    private List<BotApiMethod<?>> showFormInfo(Update update, User user, Session session){
        String chatId = String.valueOf(update.getCallbackQuery().getMessage().getChatId());
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String code = callbackQuery.getData();
        Form form = formService.getFormByCode(code);
        String createdAt = form.getCreatedAt().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String expiresAt = form.getExpiresAt().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String state = form.getExpiresAt().before(Timestamp.from(Instant.now()))?"Закрита":"Відкрита";
        String text = "Форма: %s \n Створена %s \n Закінчується: %s \n Статус %s \n Кількість відповідей: %d";
        return emptyList;
    }
}
