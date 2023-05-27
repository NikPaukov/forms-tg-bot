package com.example.polltgbot.bot.handlers;

import com.example.polltgbot.bot.handlers.createHandlers.CreateCallbackQueryHandler;
import com.example.polltgbot.bot.handlers.createHandlers.CreateMessageHandler;
import com.example.polltgbot.bot.handlers.getHandlers.GetCallbackQueryHandler;
import com.example.polltgbot.bot.handlers.getHandlers.GetMessageHandler;
import com.example.polltgbot.bot.handlers.startHandlers.StartCallbackQueryHandler;
import com.example.polltgbot.bot.handlers.startHandlers.StartMessageHandler;
import com.example.polltgbot.data.enums.Stage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class HandlersContext {
    private final StartMessageHandler startMessageHandler;
    private final StartCallbackQueryHandler startCallbackQueryHandler;
    private final CreateMessageHandler createMessageHandler;
    private final CreateCallbackQueryHandler createCallbackQueryHandler;
    private final GetMessageHandler getMessageHandler;
    private final GetCallbackQueryHandler getCallbackQueryHandler;
    public UpdateHandler getMessageHandler(Stage stage, String type) {
        System.out.println(stage.name());
        System.out.println(type);
        if (stage.equals(Stage.START)) {
            if (type.equals("message")) {
                return startMessageHandler;
            }
            if (type.equals("callbackQuery")) {
                return startCallbackQueryHandler;
            }
        } else if(stage.equals(Stage.CREATE)){
            if (type.equals("message")) {
                return createMessageHandler;
            }
            if(type.equals("callbackQuery")){
                return createCallbackQueryHandler;
            }
        }else if(stage.equals(Stage.GET)){
            if (type.equals("message")) {
                return getMessageHandler;
            }
            if (type.equals("callbackQuery")) {
                return getCallbackQueryHandler;
            }

        }
        throw new IllegalArgumentException();
    }
}
