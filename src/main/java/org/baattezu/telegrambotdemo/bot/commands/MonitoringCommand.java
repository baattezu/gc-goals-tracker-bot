package org.baattezu.telegrambotdemo.bot.commands;


import lombok.RequiredArgsConstructor;
import org.baattezu.telegrambotdemo.service.GoalService;
import org.baattezu.telegrambotdemo.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class MonitoringCommand implements Command{

    private final UserService userService;
    private final GoalService goalService;


    @Override
    public SendMessage execute(Update update) {
        return null;
    }
}
