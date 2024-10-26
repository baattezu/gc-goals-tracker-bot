package org.baattezu.telegrambotdemo.bot.commands.users;

import org.baattezu.telegrambotdemo.bot.commands.Command;
import org.baattezu.telegrambotdemo.utils.BotMessagesEnum;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.baattezu.telegrambotdemo.utils.TelegramBotHelper.setMainMenuKeyboard;

@Component
public class HelpCommand implements Command {
    @Override
    public Object execute(Update update) {
        var message = new SendMessage();
        message.setChatId(update.getMessage().getChatId());
        message.setText(BotMessagesEnum.START_WORK_WITH_BOT.getMessage());
        setMainMenuKeyboard(message);
        return message;
    }
}
