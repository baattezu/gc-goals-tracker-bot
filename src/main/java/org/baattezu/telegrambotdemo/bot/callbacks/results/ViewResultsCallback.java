package org.baattezu.telegrambotdemo.bot.callbacks.results;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baattezu.telegrambotdemo.bot.callbacks.Callback;
import org.baattezu.telegrambotdemo.bot.callbacks.CallbackHandler;
import org.baattezu.telegrambotdemo.service.GoalService;
import org.baattezu.telegrambotdemo.service.UserService;
import org.baattezu.telegrambotdemo.utils.BotMessagesEnum;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
@Slf4j
public class ViewResultsCallback implements CallbackHandler {
    private final UserService userService;
    private final GoalService goalService;

    @Override
    public Object execute(Callback callback, Update update) {
        var userIdAndIndex = callback.getData().split(":");
        long userId = Long.parseLong(userIdAndIndex[0]);
        long userIndex = Long.parseLong(userIdAndIndex[1]);
        var message = update.getCallbackQuery().getMessage();
        var text = message.getText();
        var target = "Цели и результаты " + userIndex + "#";
        if (text.contains(target)){
            log.info("не было добавлено текста");
            return null;
        }
        var newText = new StringBuilder("\n" +
                "Цели и результаты " + userIndex + "#:\n");

        var goals = goalService.getAllGoals(userId);
        var index = 1;
        for (var g : goals){
            newText.append(BotMessagesEnum.GET_GOAL_DETAIL_MESSAGE_VER1.getMessage(
                    index,  g.getCompleted() ? "✅" : "", g.getGoal(), g.getReward()));
            index++;
        }
        text += newText + "\n\n______________________";

        if (text.contains("Цели и результаты")){
            text = text.replace("Цели и результаты", "<blockquote expandable> Цели и результаты");
            text = text.replace("______________________", "______________________</blockquote>");
        }
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(message.getChatId());
        editMessageText.setMessageId(message.getMessageId());
        editMessageText.setReplyMarkup(message.getReplyMarkup());
        editMessageText.setText(text);

        editMessageText.enableHtml(true);
        return editMessageText;
    }
}
