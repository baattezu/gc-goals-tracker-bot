package org.baattezu.telegrambotdemo.bot.callbacks.goals;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baattezu.telegrambotdemo.bot.callbacks.Callback;
import org.baattezu.telegrambotdemo.bot.callbacks.CallbackHandler;
import org.baattezu.telegrambotdemo.service.GoalService;
import org.baattezu.telegrambotdemo.utils.BotMessagesEnum;
import org.baattezu.telegrambotdemo.utils.TelegramBotHelper;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
@Slf4j
public class NavigationCallback implements CallbackHandler {
    private final GoalService goalService;

    @Override
    public Object execute(Callback callback, Update update) {
        var message = update.getCallbackQuery().getMessage();
        var isGroupMessage = message.isGroupMessage();
        var chatId = message.getChatId();
        var messageId = message.getMessageId();

        String[] userIdAndUsernameFromGroupMessage = message.getText()
                .split("на эту неделю")[0]
                .split("✨ Цели ");

        var userId = !isGroupMessage ?
                update.getCallbackQuery().getFrom().getId() : Long.valueOf(userIdAndUsernameFromGroupMessage[0]);
        var currentPage = Integer.parseInt(callback.getData());
        var myGoals = goalService.getAllGoals(userId, false);
        var whoseGoals = isGroupMessage ?
                BotMessagesEnum.GOALS_OF_USER_THIS_WEEK.getMessage(
                        userId, userId, userIdAndUsernameFromGroupMessage[1].trim()) :
                BotMessagesEnum.MY_GOALS_ON_THIS_WEEK.getMessage();
        var response = new StringBuilder(whoseGoals);

        log.info(chatId.toString());
        var editMessage = new EditMessageText();
        editMessage.setChatId(chatId);
        editMessage.setMessageId(messageId);
        editMessage.enableHtml(true);
        editMessage.setReplyMarkup(TelegramBotHelper
                .setAllGoalsResponseAndKeyboard(response, myGoals, currentPage, isGroupMessage));
        editMessage.setText(response.toString());
        return editMessage;
    }
}
