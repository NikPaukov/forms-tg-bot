package com.example.polltgbot.bot.handlers.startHandlers;

import com.example.polltgbot.bot.handlers.UpdateHandler;
import com.example.polltgbot.bot.keybords.InlineKeyboards;
import com.example.polltgbot.data.entities.Form;
import com.example.polltgbot.data.entities.User;
import com.example.polltgbot.data.enums.Stage;
import com.example.polltgbot.services.PollService;
import com.example.polltgbot.services.TranslationService;
import lombok.AllArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@AllArgsConstructor
public class StartCallbackQueryHandler implements UpdateHandler {
    private final TranslationService translationService;
    private final InlineKeyboards inlineKeyboards;
    private final List<BotApiMethod<?>> emptyList;
    private final PollService pollService;
    public List<BotApiMethod<?>> handle(Update update, User user, Session session) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String data = callbackQuery.getData();
        if (data.equals("createForm")) {
            return createForm(update, user, session);
        } else if (data.equals("getForm")){
            return getForm(update, user, session);
        } else if(data.equals("viewForms")){
            return viewForms(update, user, session);
        }

        return List.of(new EditMessageReplyMarkup(String.valueOf(callbackQuery.getMessage().getChatId()), callbackQuery.getMessage().getMessageId(), callbackQuery.getInlineMessageId(), null));
    }

    public List<BotApiMethod<?>> viewForms(Update update, User user, Session session){
        session.setAttribute("stage", Stage.VIEW);
        session.setAttribute("show-forms-list", true);
        List<Form> forms = pollService.getFormsByCreator(user);
        String chatId = String.valueOf(update.getCallbackQuery().getMessage().getChatId());
        SendMessage sendMessage = new SendMessage(chatId, "Ваші опитування:");
        sendMessage.setReplyMarkup(inlineKeyboards.getFormsKeyboard(forms));
        return emptyList;
    }
    public List<BotApiMethod<?>> getForm(Update update, User user, Session session) {
        session.setAttribute("stage", Stage.GET);
        String chatId = String.valueOf(update.getCallbackQuery().getMessage().getChatId());
        if (session.getAttribute("waiting-for-code") == null || !((boolean) session.getAttribute("waiting-for-code"))) {
            session.setAttribute("waiting-for-code", true);
            return List.of(new SendMessage(chatId, translationService.getTranslation(user.getLanguage(), "request-code").getValue()));
        }
        return emptyList;
    }
        /*
        Просимо код
            Отримуємо код
        видаємо по 3 повідомлення
        змінюємо через реплай кеборд
            чекаємо поки юзер тикне /finish
         */

    private List<BotApiMethod<?>> createForm(Update update, User user, Session session) {
        String chatId = String.valueOf(update.getCallbackQuery().getMessage().getChatId());
        session.setAttribute("stage", Stage.CREATE);
        SendMessage message = new SendMessage(chatId, translationService.getTranslation(user.getLanguage(), "create-poll-msg").getValue());
        message.setReplyMarkup(inlineKeyboards.getTimeChoseKeyboard(user.getLanguage()));
    return List.of(message);
    }

}
