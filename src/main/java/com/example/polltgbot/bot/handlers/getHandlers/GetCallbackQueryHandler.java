package com.example.polltgbot.bot.handlers.getHandlers;

import com.example.polltgbot.bot.handlers.UpdateHandler;
import com.example.polltgbot.data.entities.Answer;
import com.example.polltgbot.data.entities.Form;
import com.example.polltgbot.data.entities.Message;
import com.example.polltgbot.data.entities.User;
import com.example.polltgbot.data.repositories.AnswerRepository;
import com.example.polltgbot.services.AnswerService;
import com.example.polltgbot.services.MessageService;
import com.example.polltgbot.services.UserService;
import lombok.AllArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.List;

@Component
@AllArgsConstructor
public class GetCallbackQueryHandler implements UpdateHandler {
    private final List<BotApiMethod<?>> emptyList;
    private final MessageService messageService;
    private final AnswerService answerService;
    @Override
    public List<BotApiMethod<?>> handle(Update update, User user, Session session) {
        List<Answer> answers = (List<Answer>) session.getAttribute("answers");
        Form form = (Form) session.getAttribute("form");
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String chatId = String.valueOf(callbackQuery.getMessage().getChatId());
        int plusPosition = callbackQuery.getData().indexOf("+");
        long id = Integer.parseInt(callbackQuery.getData().substring(0, plusPosition));
        String answerData = callbackQuery.getData().substring(plusPosition+1);
        Message message = messageService.findById(id);
        if(checkIfVoted(answers, message)){
            //TODO
            SendMessage sendMessage= new SendMessage(chatId, "Ви вже дали відповідь на це запитання");
            sendMessage.setReplyToMessageId(callbackQuery.getMessage().getMessageId());
            return List.of(sendMessage);
        }
        Answer answer = new Answer(form, message,  user, answerData);
        answers.add(answer);
        //TODO
        SendMessage sendMessage = new SendMessage(chatId
                , "Відповідь записано");
        sendMessage.setReplyToMessageId(callbackQuery.getMessage().getMessageId());
        return List.of(sendMessage);
    }
    private boolean checkIfVoted(List<Answer> answers, Message message){
        return answers.stream().anyMatch(o -> o.getMessage().equals(message));
    }
}
