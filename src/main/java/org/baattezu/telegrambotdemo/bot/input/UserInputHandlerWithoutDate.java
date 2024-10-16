package org.baattezu.telegrambotdemo.bot.input;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baattezu.telegrambotdemo.data.CallbackType;
import org.baattezu.telegrambotdemo.data.UserGoalData;
import org.baattezu.telegrambotdemo.data.UserState;
import org.baattezu.telegrambotdemo.model.Goal;
import org.baattezu.telegrambotdemo.service.GoalService;
import org.baattezu.telegrambotdemo.service.UserService;
import org.baattezu.telegrambotdemo.utils.BotMessagesEnum;
import org.baattezu.telegrambotdemo.utils.TelegramBotHelper;
import org.baattezu.telegrambotdemo.utils.keyboard.Markup;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.yaml.snakeyaml.error.Mark;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserInputHandlerWithoutDate {


    private final UserService userService;
    private final GoalService goalService;

    public Object handleMessage(Update update) {
        var message = update.getMessage();
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();
        int replyMessageId = message.getReplyToMessage() != null ? message.getReplyToMessage().getMessageId() : 0;
        String messageText = message.getText();

        // Получаем текущее состояние пользователя
        UserGoalData data = goalService.getUserState(userId);
        UserState state = data.userState();

        List<BotApiMethod> botApiMethodList = new ArrayList<>();
        long goalId = data.goalId();

        Goal goal = goalService.getGoalById(goalId);
        switch (state) {
            case WAITING_FOR_TITLE:
                goalService.setGoal(goal, messageText);
                goalService.setUserState(userId, UserState.WAITING_FOR_REWARD, data.goalId());
                var rewardMessage = new SendMessage(String.valueOf(chatId), "✨ Пожалуйста, опишите желаемое вознаграждение.");
                rewardMessage.setReplyMarkup(new ForceReplyKeyboard());
                botApiMethodList.add(rewardMessage);
                botApiMethodList.add(deleteMessageFromReply(chatId, replyMessageId));

                break;

            case WAITING_FOR_REWARD:
                // Сохраняем вознаграждение и заканчиваем
                goalService.setGoalReward(goal, messageText);

                var thisWeek = LocalDateTime.now().with(ChronoField.DAY_OF_WEEK, 7).withHour(23).withMinute(59);
                goalService.setGoalDeadline(goal, thisWeek);

                goalService.clearUserState(userId);  // Завершаем процесс
                var goalMadeMessage = new SendMessage();
                goalMadeMessage.setChatId(chatId);
                goalMadeMessage.setText("🎯 Цель на неделю успешно создана! Успехов! 💪✨"+
                        "<blockquote expandable>" +
                        BotMessagesEnum.GET_GOAL_DETAIL_MESSAGE_VER1.getMessage(0, "", goal.getGoal(), goal.getReward()) +
                        "</blockquote>");

                goalMadeMessage.enableHtml(true);
                goalMadeMessage.setReplyMarkup(Markup.keyboard()
                        .addRow(Markup.Button.create("Ok", CallbackType.DELETE_LAST_MESSAGES, "nothing"))
                        .build());
                botApiMethodList.add(goalMadeMessage);
                botApiMethodList.add(deleteMessageFromReply(chatId, replyMessageId));
                break;
            case WAITING_FOR_RESULTS:
                // Сохраняем описание и переходим к дедлайну
                userService.writeResultsForWeek(userId, messageText);
                goalService.clearUserState(userId);  // Завершаем процесс

                var editMessage = new EditMessageText();
                editMessage.setText(BotMessagesEnum.MY_RESULTS.getMessage(messageText));
                editMessage.setMessageId(Math.toIntExact(data.goalId()));
                editMessage.setChatId(chatId);
                editMessage.setReplyMarkup(Markup.keyboard().addRow(
                        Markup.Button.create("Переписать результаты за неделю", CallbackType.PUT_RESULTS, String.valueOf(userId))
                ).build());
                botApiMethodList.add(editMessage);
                break;
            default:
                var badResponseMessage = new SendMessage(String.valueOf(chatId), "Неизвестное состояние. Пожалуйста, начните сначала.");
                botApiMethodList.add(badResponseMessage);
                break;
        }
        // Обрабатываем в зависимости от состояния
        botApiMethodList.add(TelegramBotHelper.deletePreviousUserMessage(update));
        return botApiMethodList;
    }

    private DeleteMessage deleteMessageFromReply(long chatId, long messageId){
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId((int) messageId);
        return deleteMessage;
    }


}
