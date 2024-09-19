package org.baattezu.telegrambotdemo.bot.callbacks.results;

import lombok.RequiredArgsConstructor;
import org.baattezu.telegrambotdemo.bot.callbacks.Callback;
import org.baattezu.telegrambotdemo.bot.callbacks.CallbackHandler;
import org.baattezu.telegrambotdemo.service.GoalService;
import org.baattezu.telegrambotdemo.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class ViewResultsCallback implements CallbackHandler {
    private final UserService userService;
    private final GoalService goalService;

    @Override
    public Object execute(Callback callback, Update update) {
        EditMessageText editMessageText = new EditMessageText();
        long userId = Long.parseLong(callback.getData());
        var user = userService.findById(userId);
        

        return editMessageText;
    }
}
