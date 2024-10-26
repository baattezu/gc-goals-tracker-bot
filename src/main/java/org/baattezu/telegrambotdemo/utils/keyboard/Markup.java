package org.baattezu.telegrambotdemo.utils.keyboard;

import org.baattezu.telegrambotdemo.data.CallbackType;
import org.baattezu.telegrambotdemo.utils.JsonHandler;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Markup {

    private final List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

    private Markup() {
    }

    public static Markup keyboard() {
        return new Markup();
    }

    public Markup addRow(InlineKeyboardButton... buttons) {
        List<InlineKeyboardButton> row = new ArrayList<>(Arrays.asList(buttons));
        this.keyboard.add(row);
        return this;
    }

    public InlineKeyboardMarkup build() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);
        return markup;
    }

    // Вложенный класс для создания кнопок
    public static class Button {
        private final InlineKeyboardButton button;

        private Button(String text, CallbackType callbackType, String data) {
            this.button = new InlineKeyboardButton();
            this.button.setText(text);
            this.button.setCallbackData(JsonHandler.toJson(List.of(callbackType, data)));
        }

        private Button(String text, String url, CallbackType callbackType, String data) {
            this.button = new InlineKeyboardButton();
            this.button.setText(text);
            this.button.setUrl(url);
            this.button.setCallbackData(JsonHandler.toJson(List.of(callbackType, data)));
        }

        public static InlineKeyboardButton create(String text, CallbackType callbackType, String data) {
            return new Button(text, callbackType, data).button;
        }

        public static InlineKeyboardButton create(String text, String botUrl, CallbackType callbackType, String data) {
            return new Button(text, botUrl, callbackType, data).button;
        }
    }
}

