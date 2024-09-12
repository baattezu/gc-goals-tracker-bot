package org.baattezu.telegrambotdemo.bot.callbacks;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface CallbackHandler<T> {

    T execute(Callback callback, Update update);
}
