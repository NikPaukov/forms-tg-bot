package com.example.polltgbot.bot.Events.getEvents.process;

import com.example.polltgbot.bot.Events.Event;
import com.example.polltgbot.bot.Events.EventChain;
import com.example.polltgbot.bot.Events.eventTypes.CallbackEventType;
import com.example.polltgbot.bot.Events.eventTypes.MessageEventType;
import com.example.polltgbot.bot.Events.getEvents.GetHandleMessagesEvent;
import com.example.polltgbot.data.entities.Answer;
import com.example.polltgbot.data.entities.Form;
import com.example.polltgbot.data.entities.Message;
import com.example.polltgbot.data.entities.User;
import com.example.polltgbot.services.AnswerService;
import com.example.polltgbot.services.MessageService;
import lombok.AllArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Component
public class GetProcessCallbackEvent extends Event<CallbackEventType, MessageEventType> {
    @Override
    public Class<?> getInputEventType() {
        return CallbackEventType.class;
    }

    @Override
    public Class<?> getOutputEventType() {
        return MessageEventType.class;
    }
    private final List<BotApiMethod<?>> emptyList;
    private final MessageService messageService;
    private final AnswerService answerService;
    private final GetHandleMessagesEvent getHandleMessagesEvent;

    @Override
    public List<BotApiMethod<?>> emmit(Update update, User user, Session session,EventChain chain) {
        String chatId = String.valueOf(update.getCallbackQuery().getMessage().getChatId());
        Map<String, Object> eventChainData = chain.getEventChainData();

        List<Answer> answers = (List<Answer>) eventChainData.get("answers");
        Form form = (Form) eventChainData.get("form");
        CallbackQuery callbackQuery = update.getCallbackQuery();

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
        Answer answer = new Answer(message,  user, answerData);
        answers.add(answer);
        //TODO
        SendMessage sendMessage = new SendMessage(chatId
                , "Відповідь записано");
        sendMessage.setReplyToMessageId(callbackQuery.getMessage().getMessageId());
        setNewEvent = true;
        chain.setCurrentEvent(getHandleMessagesEvent);
        return List.of(sendMessage);
    }
    private boolean checkIfVoted(List<Answer> answers, Message message){
        return answers.stream().anyMatch(o -> o.getMessage().equals(message));
    }
}
