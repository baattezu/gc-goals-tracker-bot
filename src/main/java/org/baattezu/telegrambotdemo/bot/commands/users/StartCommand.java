package org.baattezu.telegrambotdemo.bot.commands.users;

import lombok.RequiredArgsConstructor;
import org.baattezu.telegrambotdemo.bot.commands.Command;
import org.baattezu.telegrambotdemo.data.CallbackType;
import org.baattezu.telegrambotdemo.service.UserService;
import org.baattezu.telegrambotdemo.utils.BotMessagesEnum;
import org.baattezu.telegrambotdemo.utils.JsonHandler;
import org.baattezu.telegrambotdemo.utils.keyboard.Markup;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.PinChatMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StartCommand implements Command {

    private final UserService userService;
    @Override
    public List<BotApiMethod> execute(Update update) {
        var chatId = update.getMessage().getChatId();
        var userId = update.getMessage().getFrom().getId();

        var message = new SendMessage();
        message.setChatId(chatId);
        List<BotApiMethod> listOfMethods = new ArrayList<>();

        if (update.getMessage().isGroupMessage()){
            return null;
        }
        if (userService.isRegistered(userId)){
            message.setText(BotMessagesEnum.START_WORK_WITH_BOT.getMessage());
            setMainMenuKeyboard(message);
            listOfMethods.add(message);
            var deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(chatId);
            deleteMessage.setMessageId(update.getMessage().getMessageId());
            listOfMethods.add(deleteMessage);
            return listOfMethods;
        }
        var firstName = update.getMessage().getFrom().getFirstName();
        var lastName = update.getMessage().getFrom().getLastName();
        String username = firstName + (lastName != null ? " " + lastName : "");

        userService.registerUser(userId, username, chatId);
        message.setText(BotMessagesEnum.REGISTER_SUCCESS_MESSAGE.getMessage(username));
        message.setReplyMarkup(Markup.keyboard()
                .addRow(Markup.Button.create("Начать работу с ботом", CallbackType.START_WORK_WITH_BOT, "niggamove"))
                .build());
        listOfMethods.add(message);
        var deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(update.getMessage().getMessageId());
        listOfMethods.add(deleteMessage);
        return listOfMethods;
    }

    public void setMainMenuKeyboard(SendMessage sendMessage){
        // Создаем клавиатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true); // Клавиатура будет адаптироваться под экран
        replyKeyboardMarkup.setOneTimeKeyboard(false); // Клавиатура останется открытой

        // Создаем список строк с кнопками
        List<KeyboardRow> keyboard = new ArrayList<>();

        // Создаем первую строку клавиатуры с кнопками
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("/set_goal"));
        row1.add(new KeyboardButton("/goals"));

        // Создаем вторую строку
        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("/progress"));
        row2.add(new KeyboardButton("/results"));

        // Добавляем строки клавиатуры
        keyboard.add(row1);
        keyboard.add(row2);

        // Устанавливаем клавиатуру в объект
        replyKeyboardMarkup.setKeyboard(keyboard);

        // Присоединяем клавиатуру к сообщению
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
    }
}
