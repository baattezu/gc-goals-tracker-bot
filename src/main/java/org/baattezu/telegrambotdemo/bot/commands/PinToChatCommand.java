package org.baattezu.telegrambotdemo.bot.commands;


import lombok.RequiredArgsConstructor;
import org.baattezu.telegrambotdemo.service.UserService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PinToChatCommand {

    private final UserService userService;

}
