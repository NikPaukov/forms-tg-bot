package com.example.polltgbot.bot.handlers.getHandlers;

import com.example.polltgbot.bot.handlers.UpdateHandler;
import com.example.polltgbot.bot.keybords.InlineKeyboards;
import com.example.polltgbot.bot.keybords.ReplyKeyboards;
import com.example.polltgbot.data.PollDTO;
import com.example.polltgbot.data.entities.Answer;
import com.example.polltgbot.data.entities.Message;
import com.example.polltgbot.data.entities.Form;
import com.example.polltgbot.data.entities.User;
import com.example.polltgbot.data.enums.MessageTypeEnum;
import com.example.polltgbot.data.enums.Stage;
import com.example.polltgbot.services.AnswerService;
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
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class GetMessageHandler implements UpdateHandler {
    private final PollService formService;
    private final List<BotApiMethod<?>> emptyList;
    private final TranslationService translationService;
    private final MessageService messageService;
    private final MessageDeserializersHandler messageDeserializersHandler;
    private final AnswerService answerService;
    private final ReplyKeyboards replyKeyboards;
    private final InlineKeyboards inlineKeyboards;

    @Override
    public List<BotApiMethod<?>> handle(Update update, User user, Session session) {
        if ((boolean) session.getAttribute("waiting-for-code")) {
            return handleCode(update, user, session);
        }
        if (update.getMessage().getText().equals("/next")) {
            return processForm(update, user, session, (Form) session.getAttribute("form"));
        }
        if (update.getMessage().getText().equals("/finish")) {
            return handleFinish(update, user, session);
        }
        //оброблюємо /next
        //оброблюємо /finissh
        return emptyList;
    }

    public List<BotApiMethod<?>> handleFinish(Update update, User user, Session session) {
        String chatId = String.valueOf(update.getMessage().getChatId());
        List<Answer> answers = (List<Answer>) session.getAttribute("answers");
        answers.forEach(answerService::createAnswer);
        cleanSession(session);
        return List.of(new SendMessage(chatId, translationService.getTranslation(user.getLanguage(), "finish-msg").getValue()));

    }

    private void cleanSession(Session session) {
        session.setAttribute("waiting-for-code", false);
        session.setAttribute("stage", Stage.START);
        session.setAttribute("code", null);
        session.setAttribute("poll", null);
        session.setAttribute("passed", null);
        session.setAttribute("answers", null);

    }

    public List<BotApiMethod<?>> handleCode(Update update, User user, Session session) {
        session.setAttribute("waiting-for-code", false);
        String chatId = String.valueOf(update.getMessage().getChatId());
        Form form;
        try {
            form = formService.getFormByCode(update.getMessage().getText());
        } catch (IllegalArgumentException e) {
            cleanSession(session);
            return List.of(new SendMessage(chatId, translationService.getTranslation(user.getLanguage(), "wrong-code").getValue()));
        }
        if (answerService.hasUserPassedForm(form, user)) {
            cleanSession(session);
            //TODO
            return List.of(new SendMessage(chatId, "Ви вже пройшли цю форму"));
        }
        if(form.getExpiresAt().before(Timestamp.from(Instant.now()))){
            cleanSession(session);
            //TODO
            return List.of(new SendMessage(chatId, "Час виконання цієї форми закінчився"));
        }
        session.setAttribute("form", form);
        session.setAttribute("passed", 0);
        session.setAttribute("answers", new ArrayList<Answer>());
        SendMessage sendMessageWithReply = new SendMessage(chatId, translationService.getTranslation(user.getLanguage(), "get-begging").getValue());
        sendMessageWithReply.setReplyMarkup(replyKeyboards.getFormKeyboard());
        SendMessage sendFormNameMessage = new SendMessage(chatId, "Форма: " + form.getName());
        List<BotApiMethod<?>> res = processForm(update, user, session, form);
        res.add(0, sendMessageWithReply);
        res.add(1, sendFormNameMessage);

        session.setAttribute("code", update.getMessage().getText());
        return res;
    }

    private List<BotApiMethod<?>> processForm(Update update, User user, Session session, Form poll) {
        String chatId = String.valueOf(update.getMessage().getChatId());
        int index = (int) session.getAttribute("passed");
        List<BotApiMethod<?>> res = new ArrayList<>();
        List<Message> messages = messageService.findAllByPoll(poll);
        List<Message> filteredMessages = messages.stream().skip(index).limit(3).toList();
        System.out.println(messages);
        System.out.println(filteredMessages);
        session.setAttribute("passed", index + filteredMessages.size());
        if (filteredMessages.size() == 0) {
            res.add(new SendMessage(chatId, translationService.getTranslation(user.getLanguage(), "form-done").getValue()));
            return res;
        }
        for (Message message : filteredMessages) {
            MessageDeserializer messageDeserializer = messageDeserializersHandler.getMessageSerializer(message.getMessageType());
            Object answer;
            try {
                answer = messageDeserializer.deserialize(message.getAnswer());
            } catch (JsonProcessingException e) {
                cleanSession(session);
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
