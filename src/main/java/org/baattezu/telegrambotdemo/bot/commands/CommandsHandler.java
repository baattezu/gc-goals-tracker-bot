package org.baattezu.telegrambotdemo.bot.commands;

import lombok.Data;
import org.baattezu.telegrambotdemo.bot.commands.goals.*;
import org.baattezu.telegrambotdemo.bot.commands.pinning.PinToChatCommand;
import org.baattezu.telegrambotdemo.bot.commands.results.GetPhoto;
import org.baattezu.telegrambotdemo.bot.commands.results.Top5Results;
import org.baattezu.telegrambotdemo.bot.commands.users.CheckInCommand;
import org.baattezu.telegrambotdemo.bot.commands.users.ChangeNameCommand;
import org.baattezu.telegrambotdemo.bot.commands.users.StartCommand;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
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
            GetAllUsersFromThisChatCommand getAllUsersFromThisChatCommand,
            WriteResultsCommand writeResultsCommand,
            GetPhoto getPhoto,
            Top5Results top5results
    ) {
        this.commands = new HashMap<>();
        this.commands.put("/start", startCommand);
        this.commands.put("/change_name", changeNameCommand);
        this.commands.put("/check_in", checkInCommand);
        this.commands.put("/set_goal", setGoalCommand);
        this.commands.put("/goals", getMyGoalsCommand);
        this.commands.put("/pin_to_chat", pinToChatCommand);
        this.commands.put("/list_users", getAllUsersFromThisChatCommand);
        this.commands.put("/results", writeResultsCommand);
        this.commands.put("/progress", getPhoto);
        this.commands.put("/top5", top5results);
    }

    public Object handleCommand(Update update) {
        String messageText = update.getMessage().getText();

        if (messageText.contains("@")) {
            messageText = messageText.split("@")[0];
        }
        String command = messageText.split(" ")[0]; // Первая часть текста — это команда
        long chatId = update.getMessage().getChatId();

        Command<?> commandHandler = commands.get(command);
        if (commandHandler != null) {
            return commandHandler.execute(update);
        } else {
            return new SendMessage(String.valueOf(chatId), "Неизвестная команда");
        }
    }
}
