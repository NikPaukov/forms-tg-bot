package com.example.polltgbot.bot.keybords;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

@Component
@AllArgsConstructor
public class ReplyKeyboards {
    public ReplyKeyboardMarkup getFormKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(List.of(new KeyboardRow(
                        List.of(new KeyboardButton("/next"),
                                new KeyboardButton("/finish"),
                                new KeyboardButton("/cancel"))),
                new KeyboardRow(
                        List.of(new KeyboardButton("/poll"),
                                new KeyboardButton("/poll_end"),
                                new KeyboardButton("/poll_cancel")))));
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup createFormKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(List.of(new KeyboardRow(
                List.of(new KeyboardButton("/create"),
                        new KeyboardButton("/cancel")))));
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }
}
