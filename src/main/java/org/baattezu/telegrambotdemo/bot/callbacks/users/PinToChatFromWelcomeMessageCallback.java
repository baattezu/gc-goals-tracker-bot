package org.baattezu.telegrambotdemo.bot.callbacks.users;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baattezu.telegrambotdemo.bot.callbacks.Callback;
import org.baattezu.telegrambotdemo.bot.callbacks.CallbackHandler;
import org.baattezu.telegrambotdemo.service.ChatService;
import org.baattezu.telegrambotdemo.service.UserService;
import org.baattezu.telegrambotdemo.utils.TelegramBotHelper;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class PinToChatFromWelcomeMessageCallback implements CallbackHandler {
    private final UserService userService;
    private final ChatService chatService;
    private final Set<Long> userIdCache = new HashSet<>(); // Кэшируемый набор userId

    @Override
    public Object execute(Callback callback, Update update) {
        var messageId = update.getCallbackQuery().getMessage().getMessageId();
        var userId = update.getCallbackQuery().getFrom().getId();
        var chatId = update.getCallbackQuery().getMessage().getChatId();

        // Проверка в кэше: если пользователь уже есть, пропускаем проверку базы данных
        if (!userIdCache.contains(userId)) {
            if (!userService.isRegistered(userId)) {
                return null;
            }
            userIdCache.add(userId);
        }

        var user = userService.findById(userId);
        var chat = chatService.findById(chatId);

        userService.pinToChat(user, chat);

        var sendMessage = new SendMessage();
        sendMessage.setChatId(user.getPrivateChatId());
        sendMessage.setText("Вы успешно прикрепились к чату: "+ chat.getName());
        return sendMessage;
    }

}
