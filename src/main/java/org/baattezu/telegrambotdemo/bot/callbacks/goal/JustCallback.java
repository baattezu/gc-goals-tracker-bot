package org.baattezu.telegrambotdemo.bot.callbacks.goal;

import org.baattezu.telegrambotdemo.bot.callbacks.Callback;
import org.baattezu.telegrambotdemo.bot.callbacks.CallbackHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class JustCallback implements CallbackHandler {


    @Override
    public Object execute(Callback callback, Update update) {

        return null;
    }
}
