package org.baattezu.telegrambotdemo.bot;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.baattezu.telegrambotdemo.bot.commands.*;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

@Component
@Data
public class CommandsHandler {

    private final Map<String, Command> commands;

    public CommandsHandler(
            RegisterCommand registerCommand,
            CheckInCommand checkInCommand,
            SetGoalCommand setGoalCommand,
            GetMyGoalsCommand getMyGoalsCommand
    ) {
        this.commands = Map.of(
                "/register", registerCommand,
                "/checkin", checkInCommand,
                "/setgoal", setGoalCommand,
                "/getgoals", getMyGoalsCommand
        );
    }

    public SendMessage handleCommand(Update update) {
        String messageText = update.getMessage().getText();
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
