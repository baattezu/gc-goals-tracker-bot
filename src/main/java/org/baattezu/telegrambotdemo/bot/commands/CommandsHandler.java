package org.baattezu.telegrambotdemo.bot.commands;

import lombok.Data;
import org.baattezu.telegrambotdemo.bot.commands.*;
import org.baattezu.telegrambotdemo.bot.commands.goalsrelated.*;
import org.baattezu.telegrambotdemo.bot.commands.pinning.PinToChatCommand;
import org.baattezu.telegrambotdemo.bot.commands.pinning.UnpinFromChatCommand;
import org.baattezu.telegrambotdemo.bot.commands.usersrelated.CheckInCommand;
import org.baattezu.telegrambotdemo.bot.commands.usersrelated.ChangeNameCommand;
import org.baattezu.telegrambotdemo.bot.commands.usersrelated.StartCommand;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

@Component
@Data
public class CommandsHandler {

    private final Map<String, Command> commands;

    public CommandsHandler(
            StartCommand startCommand,
            ChangeNameCommand changeNameCommand,
            CheckInCommand checkInCommand,
            SetGoalCommand setGoalCommand,
            GetMyGoalsCommand getMyGoalsCommand,
            PinToChatCommand pinToChatCommand,
            UnpinFromChatCommand unpinFromChatCommand,
            GetAllUsersFromThisChatCommand getAllUsersFromThisChatCommand,
            WriteResultsCommand writeResultsCommand,
            GetPhoto getPhoto
    ) {
        this.commands = Map.of(
                "/start", startCommand,
                "/change_name", changeNameCommand,
                "/check_in", checkInCommand,
                "/set_goal", setGoalCommand,
                "/my_goals", getMyGoalsCommand,
                "/pin_to_chat", pinToChatCommand,
                "/unpin_from_chat", unpinFromChatCommand,
                "/list_users", getAllUsersFromThisChatCommand,
                "/results" ,writeResultsCommand,
                "/get_photo", getPhoto
        );
    }

    public SendMessage handleCommand(Update update) {
        String messageText = update.getMessage().getText();

        if (messageText.contains("@")) {
            messageText = messageText.split("@")[0];
        }
        String command = messageText.split(" ")[0]; // Первая часть текста — это команда
        long chatId = update.getMessage().getChatId();

        Command commandHandler = commands.get(command);
        if (commandHandler != null) {
            return commandHandler.execute(update);
        } else {
            return new SendMessage(String.valueOf(chatId), "Неизвестная команда");
        }
    }
}
