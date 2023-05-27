package com.example.polltgbot.bot.handlers.createHandlers;

import com.example.polltgbot.bot.handlers.UpdateHandler;
import com.example.polltgbot.data.PollDTO;
import com.example.polltgbot.data.entities.Form;
import com.example.polltgbot.data.entities.User;
import com.example.polltgbot.data.enums.MessageTypeEnum;
import com.example.polltgbot.data.enums.Stage;
import com.example.polltgbot.services.MessageService;
import com.example.polltgbot.services.PollService;
import com.example.polltgbot.services.TranslationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
public class CreateMessageHandler implements UpdateHandler {
    private final TranslationService translationService;
    private final PollService formService;
    private final MessageService messageService;
    private final List<BotApiMethod<?>> emptyList;
    private final ObjectMapper objectMapper;


    private List<BotApiMethod<?>> noTimeChosenCase(User user, String chatId) {
        return List.of(new SendMessage(chatId, translationService.getTranslation(user.getLanguage(), "expire-not-specified").getValue()));

    }

    private void cleanSession(Session session) {
        session.setAttribute("stage", Stage.START);
        session.setAttribute("form-not-created", null);
        session.setAttribute("poll", null);
        session.setAttribute("waiting-for-question", null);
        session.setAttribute("waiting-for-options", null);
        session.setAttribute("waiting-for-name", null);
    }

    @Override
    public List<BotApiMethod<?>> handle(Update update, User user, Session session) {
        Message message = update.getMessage();
        String chatId = String.valueOf(message.getChatId());


        if (session.getAttribute("form-not-created") == null) {
            return noTimeChosenCase(user, chatId);
        } else if (!Objects.isNull(session.getAttribute("waiting-for-name"))) {
                return setFormName(session, message, chatId);
        } else if (message.isCommand() && message.getText().equals("/create")) {
            return endFormCreationCase(session, chatId);
        } else if (message.isCommand() && message.getText().equals("/poll")) {
            return startPollCreation(session, chatId, user.getLanguage());
        } else if (message.isCommand() && !Objects.isNull(session.getAttribute("waiting-for-options"))
                && message.getText().equals("/endpoll")) {
            return endPollCreation(session, chatId, user.getLanguage());
        } else if (!Objects.isNull(session.getAttribute("poll"))) {
            return processPollCreation(session, message, chatId, user.getLanguage());
        } else if (message.hasText()) {
            return handleTextMessage(session, message);
        }
        return emptyList;
    }
    private List<BotApiMethod<?>> setFormName(Session session, Message message, String chatId){
        if(message.hasText()){
            Form form = (Form) session.getAttribute("form-not-created");
            form.setName(message.getText());
            session.setAttribute("waiting-for-name", null);
            //TODO
            return List.of(new SendMessage(chatId, "Чудово, тепер введіть все що необхідно і напишіть /create для завершення"));
        }
        return emptyList;
    }

    private List<BotApiMethod<?>> startPollCreation(Session session, String chatId, String language) {
        PollDTO pollDTO = new PollDTO(null, new ArrayList<>());
        session.setAttribute("poll", pollDTO);
        session.setAttribute("waiting-for-question", true);
        return List.of(
                new SendMessage(chatId, translationService.getTranslation(language, "poll-creation-msg").getValue()));
    }

    private List<BotApiMethod<?>> processPollCreation(Session session, Message message, String chaId, String language) {
        PollDTO pollDTO = (PollDTO) session.getAttribute("poll");
        if (!Objects.isNull(session.getAttribute("waiting-for-question"))) {
            pollDTO.setQuestion(message.getText());
            session.setAttribute("waiting-for-question", null);
            session.setAttribute("waiting-for-options", true);
            return List.of(
                    new SendMessage(chaId, translationService.getTranslation(language, "poll-options-msg").getValue()));
        } else if (!Objects.isNull(session.getAttribute("waiting-for-options"))) {
            if (message.hasText()) {
                pollDTO.getOptions().add(message.getText());
            } else {
                //TODO
                return List.of(new SendMessage(chaId, "Цей тип даних не допускається"));
            }
        }
        return emptyList;
    }

    private List<BotApiMethod<?>> endPollCreation(Session session, String chatId, String language) {

        if (Objects.isNull(session.getAttribute("order"))) {
            session.setAttribute("order", 0);
        }
        int order = (int) session.getAttribute("order");
        session.setAttribute("order", order + 1);

        Form form = (Form) session.getAttribute("form-not-created");
        try {
            String res = objectMapper.writeValueAsString((PollDTO) session.getAttribute("poll"));
            com.example.polltgbot.data.entities.Message message = new com.example.polltgbot.data.entities.Message(
                    form, order, MessageTypeEnum.POLL, res);
            session.setAttribute("poll", null);
            session.setAttribute("waiting-for-question", null);
            session.setAttribute("waiting-for-options", null);
            ((List<com.example.polltgbot.data.entities.Message>) session.getAttribute("form-messages-not-created"))
                    .add(message);

            //Todo
            return List.of(new SendMessage(chatId, "Опитування створено!"));
        } catch (JsonProcessingException e) {
            //TODO
            cleanSession(session);
            return List.of(new SendMessage(chatId, "ПОМИЛКА"));
        }

    }

    private List<BotApiMethod<?>> handleTextMessage(Session session, Message message) {
        if (Objects.isNull(session.getAttribute("order"))) {
            session.setAttribute("order", 0);
        }
        int order = (int) session.getAttribute("order");
        session.setAttribute("order", order + 1);
        Form form = (Form) session.getAttribute("form-not-created");

        com.example.polltgbot.data.entities.Message messageDTO = new com.example.polltgbot.data.entities.Message(
                form, order, MessageTypeEnum.TEXT, message.getText()
        );
        ((List<com.example.polltgbot.data.entities.Message>) session.getAttribute("form-messages-not-created"))
                .add(messageDTO);
        return emptyList;
    }

    private List<BotApiMethod<?>> endFormCreationCase(Session session, String chatId) {
        Form form = (Form) session.getAttribute("form-not-created");
        List<com.example.polltgbot.data.entities.Message> messages =
                (List<com.example.polltgbot.data.entities.Message>) session.getAttribute("form-messages-not-created");
        formService.createForm(form);
        messageService.createMessages(messages);
        SendMessage sendMessage = new SendMessage(chatId, "Чудово! опитування створено, його унікальна назва - ```" +
                form.getCode() + "```. Використовуй її щоб надавати доступ іншим");
        sendMessage.enableMarkdown(true);
        cleanSession(session);
        return List.of(sendMessage);

    }

}
