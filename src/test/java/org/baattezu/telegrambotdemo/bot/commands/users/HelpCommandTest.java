package org.baattezu.telegrambotdemo.bot.commands.users;

import static org.junit.jupiter.api.Assertions.*;

import org.baattezu.telegrambotdemo.utils.BotMessagesEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HelpCommandTest {

    @InjectMocks
    private HelpCommand helpCommand;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExecute() {
        // Мокируем Update и Message
        Update update = new Update();
        Message message = new Message();
        Chat chat = new Chat();
        chat.setId(12345L);
        message.setChat(chat);
        update.setMessage(message);

        // Выполняем команду
        SendMessage response = (SendMessage) helpCommand.execute(update);

        // Проверяем корректность установки текста и чата
        assertEquals(String.valueOf(chat.getId()), response.getChatId());
        assertEquals(BotMessagesEnum.START_WORK_WITH_BOT.getMessage(), response.getText());

        // Проверяем наличие клавиатуры
        assertTrue(response.getReplyMarkup() instanceof ReplyKeyboardMarkup);

        // Проверяем, что клавиатура содержит правильные кнопки
        ReplyKeyboardMarkup keyboardMarkup = (ReplyKeyboardMarkup) response.getReplyMarkup();
        assertTrue(keyboardMarkup.getKeyboard().size() > 0); // Клавиатура содержит строки
        KeyboardRow row1 = keyboardMarkup.getKeyboard().get(0);
        assertEquals("/set_goal", row1.get(0).getText()); // Проверяем первую кнопку в первой строке
        assertEquals("/goals", row1.get(1).getText()); // Проверяем вторую кнопку в первой строке

        KeyboardRow row2 = keyboardMarkup.getKeyboard().get(1);
        assertEquals("/progress", row2.get(0).getText()); // Проверяем первую кнопку во второй строке
        assertEquals("/results", row2.get(1).getText()); // Проверяем вторую кнопку во второй строке
    }
}
