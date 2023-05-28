package com.example.polltgbot.bot.keybords;

import com.example.polltgbot.data.entities.Form;
import com.example.polltgbot.services.TranslationService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

@Component
@Data
@AllArgsConstructor
public class InlineKeyboards {
    private final TranslationService translationService;
    public InlineKeyboardMarkup getOptionsKeyboard(List<String> options, long id){
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        for(String option: options){
            buttons.add(List.of(getButton(option, id+"+"+option)));
        }
        final InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(buttons);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getFormsKeyboard(List<Form> forms){
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        ListIterator<Form> formListIterator = forms.listIterator();
        List<InlineKeyboardButton> row = new ArrayList<>();
        while (formListIterator.hasNext()){
            Form form1 = formListIterator.next();
            row.add(getButton(form1.getName(), form1.getCode()));
            if(formListIterator.hasNext()){
                Form form2 = formListIterator.next();
                row.add(getButton(form2.getName(), form2.getCode()));
            }
            buttons.add(row);
            row.clear();
        }
        final InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(buttons);
        return inlineKeyboardMarkup;
    }
    public InlineKeyboardMarkup  getTimeChoseKeyboard(String lg){
             List<List<InlineKeyboardButton>> buttons  = new ArrayList<>();
        buttons.add(List.of(
                getButton(translationService.getTranslation(lg, "time-15-m").getValue(), "time-15"),
                getButton(translationService.getTranslation(lg, "time-30-m").getValue(), "time-30"),
                getButton(translationService.getTranslation(lg, "time-1-h").getValue(), "time-60"),
                getButton(translationService.getTranslation(lg, "time-3-h").getValue(), "time-180")
                ));
        buttons.add(List.of(
                getButton(translationService.getTranslation(lg, "time-6-h").getValue(), "time-360"),
                getButton(translationService.getTranslation(lg, "time-12-h").getValue(), "time-720"),
                getButton(translationService.getTranslation(lg, "time-1-d").getValue(), "time-1440")
                ));
        buttons.add(List.of(
                getButton(translationService.getTranslation(lg, "time-3-d").getValue(), "time-4320"),
                getButton(translationService.getTranslation(lg, "time-7-d").getValue(), "time-10080"),
                getButton(translationService.getTranslation(lg, "time-30-d").getValue(), "time-43200")
                ));
        final InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(buttons);

        return inlineKeyboardMarkup;
    }
    public InlineKeyboardMarkup getStartKeyboard(String lg){
        List<List<InlineKeyboardButton>>buttons  = List.of(new ArrayList<>(), new ArrayList<>());
        buttons.get(0).add(getButton(translationService.getTranslation(lg, "main-keyboard-create-form").getValue(),
                "create"));
        buttons.get(0).add(getButton(translationService.getTranslation(lg, "main-keyboard-get-form").getValue(),
                "get"));
        buttons.get(1).add(getButton("Change language",
                "changeLanguage"));
        //TODO
buttons.get(1).add(getButton(translationService.getTranslation(lg, "main-keyboard-view-forms").getValue(),
        "view"));


        final InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(buttons);


        return inlineKeyboardMarkup;
    }
    private InlineKeyboardButton getButton(String buttonName, String buttonCallBackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(buttonName);
        button.setCallbackData(buttonCallBackData);

        return button;
    }
}
