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
import org.baattezu.telegrambotdemo.utils.JsonHandler;
import org.baattezu.telegrambotdemo.utils.TelegramBotHelper;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
        Long userId = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();

        // Получаем текущее состояние пользователя
        UserGoalData data = goalService.getUserState(userId);
        UserState state = data.userState();

        Object responseMessage = null;

        int messageLength = messageText.length();
        int remainingCharacters = 255 - messageLength;

        if (data != null){
            Goal goal = goalService.getGoalById(data.goalId());
            switch (state) {
                case WAITING_FOR_TITLE:
                    if (messageText.length() > 255){
                        return new SendMessage(String.valueOf(chatId),
                                "⚠️ Превышение лимита на: " + Math.abs(remainingCharacters) + " символов." +
                                        "\n✂️ Пожалуйста, укоротите сообщение с целью. 😅");
                    }
                    // Сохраняем цель
                    goalService.setGoalName(goal, messageText);
                    goalService.setUserState(userId, UserState.WAITING_FOR_REWARD, data.goalId());
                    responseMessage = new SendMessage(String.valueOf(chatId), "✨ Пожалуйста, опишите желаемое вознаграждение.");
                    break;

                case WAITING_FOR_REWARD:
                    // Сохраняем вознаграждение и заканчиваем
                    goalService.setGoalReward(goal, messageText);

                    var thisWeek = LocalDateTime.now().with(ChronoField.DAY_OF_WEEK, 7).withHour(23).withMinute(59);
                    goalService.setGoalDeadline(goal, thisWeek);

                    goalService.clearUserState(userId);  // Завершаем процесс
                    responseMessage = new SendMessage(String.valueOf(chatId), "🎯 Цель на неделю успешно создана! Успехов! 💪✨");
                    break;
                case WAITING_FOR_RESULTS:

                    if (messageText.length() > 255){
                        return new SendMessage(String.valueOf(chatId),
                                "⚠️ Превышение лимита на: " + Math.abs(remainingCharacters) + " символов." +
                                        "\n✂️ Пожалуйста, укоротите сообщение с результатами. 😅");
                    }
                    // Сохраняем описание и переходим к дедлайну
                    userService.writeResultsForWeek(userId, messageText);
                    goalService.clearUserState(userId);  // Завершаем процесс

                    var editMessage = new EditMessageText();
                    editMessage.setText(BotMessagesEnum.MY_RESULTS.getMessage(messageText));
                    editMessage.setMessageId(Math.toIntExact(data.goalId()));
                    editMessage.setChatId(update.getMessage().getChatId());
                    TelegramBotHelper.addResultsButtons(editMessage, userId);
                    responseMessage = editMessage;
//                    responseMessage = new SendMessage(String.valueOf(chatId), "Спасибо, что поделились своими результатами! 📊 Ожидайте общую таблицу участников в конце недели. 👍");
                    break;
                default:
                    responseMessage = new SendMessage(String.valueOf(chatId), "Неизвестное состояние. Пожалуйста, начните сначала.");
                    break;
            }
        }
        // Обрабатываем в зависимости от состояния
        return responseMessage;
    }


}
