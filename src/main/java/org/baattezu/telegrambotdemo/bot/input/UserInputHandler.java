package org.baattezu.telegrambotdemo.bot.input;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baattezu.telegrambotdemo.data.CallbackType;
import org.baattezu.telegrambotdemo.data.UserGoalData;
import org.baattezu.telegrambotdemo.data.UserState;
import org.baattezu.telegrambotdemo.model.Goal;
import org.baattezu.telegrambotdemo.service.GoalService;
import org.baattezu.telegrambotdemo.service.UserService;
import org.baattezu.telegrambotdemo.utils.JsonHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserInputHandler {

    private final GoalService goalService;

    public SendMessage handleMessage(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();

        // Получаем текущее состояние пользователя
        UserGoalData data = goalService.getUserState(userId);
        UserState state = data.userState();

        log.info(String.valueOf(data) + " NULLL  БЛИН");

        var formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        SendMessage responseMessage = null;
        if (data != null){
            Goal goal = goalService.getGoalById(data.goalId());
            switch (state) {
                case WAITING_FOR_TITLE:
                    // Сохраняем название и переходим к описанию
                    goalService.setGoalName(goal, messageText);
                    goalService.setUserState(userId, UserState.WAITING_FOR_DESCRIPTION, data.goalId());
                    responseMessage = new SendMessage(String.valueOf(chatId), "Название сохранено. Пожалуйста, введите описание цели.");
                    break;
                case WAITING_FOR_DESCRIPTION:
                    // Сохраняем описание и переходим к дедлайну
                    goalService.setGoalDescription(goal, messageText);
                    goalService.setUserState(userId, UserState.WAITING_FOR_DEADLINE, data.goalId());
                    responseMessage = new SendMessage(String.valueOf(chatId), "Описание сохранено. Теперь выберете дедлайн или введите вручную в формате 'dd.MM.yyyy hh:mm (12.12.2012 12:30)");
                    addKeyboardMarkup(responseMessage);
                    break;


                case WAITING_FOR_DEADLINE:
                    LocalDateTime dateTime;
                    try {
                        // Попытка парсинга дедлайна, если введен текст
                        dateTime = LocalDateTime.parse(messageText, formatter);
                        goal = goalService.setGoalDeadline(goal, dateTime);
                        goalService.clearUserState(userId);  // Завершаем процесс

                        // Формируем итоговое сообщение с целью
                        String goalSummary = String.format("Цель успешно создана!\nНазвание: %s\nОписание: %s\nДедлайн: %s",
                                goal.getGoalName(), goal.getDescription(), goal.getDeadline().format(formatter));
                        responseMessage = new SendMessage(String.valueOf(chatId), goalSummary);
                    } catch (DateTimeParseException e) {
                        // Если формат даты неправильный
                        responseMessage = new SendMessage(String.valueOf(chatId), "Некорректный формат даты. Пожалуйста, введите дату в формате 'dd.MM.yyyy hh:mm'.");
                    }
                    break;
                default:
                    responseMessage = new SendMessage(String.valueOf(chatId), "Неизвестное состояние. Пожалуйста, начните сначала.");
                    break;
            }
        }
        // Обрабатываем в зависимости от состояния
        return responseMessage;
    }

    private void addKeyboardMarkup(SendMessage message) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline3 = new ArrayList<>();


        InlineKeyboardButton today = new InlineKeyboardButton();
        today.setText("Сегодня");
        String deadlineToday = JsonHandler.toJson(List.of(CallbackType.ENTER_DEADLINE, "deadline_today"));
        today.setCallbackData(deadlineToday);
        rowInline1.add(today);

        InlineKeyboardButton tomorrow = new InlineKeyboardButton();
        tomorrow.setText("Завтра");
        String deadlineTomorrow = JsonHandler.toJson(List.of(CallbackType.ENTER_DEADLINE, "deadline_tomorrow"));
        tomorrow.setCallbackData(deadlineTomorrow);
        rowInline1.add(tomorrow);

        InlineKeyboardButton thisWeek = new InlineKeyboardButton();
        thisWeek.setText("Конец недели");
        String deadlineThisWeek = JsonHandler.toJson(List.of(CallbackType.ENTER_DEADLINE, "deadline_this_week"));
        thisWeek.setCallbackData(deadlineThisWeek);
        rowInline2.add(thisWeek);

        InlineKeyboardButton nextWeek = new InlineKeyboardButton();
        nextWeek.setText("Конец следующей недели");
        String deadlineNextWeek = JsonHandler.toJson(List.of(CallbackType.ENTER_DEADLINE, "deadline_next_week"));
        nextWeek.setCallbackData(deadlineNextWeek);
        rowInline2.add(nextWeek);

        InlineKeyboardButton thisMonth = new InlineKeyboardButton();
        thisMonth.setText("Конец месяца");
        String deadlineThisMonth = JsonHandler.toJson(List.of(CallbackType.ENTER_DEADLINE, "deadline_this_month"));
        thisMonth.setCallbackData(deadlineThisMonth);
        rowInline3.add(thisMonth);

        InlineKeyboardButton thisYear = new InlineKeyboardButton();
        thisYear.setText("Конец года");
        String deadlineThisYear = JsonHandler.toJson(List.of(CallbackType.ENTER_DEADLINE, "deadline_this_year"));
        thisYear.setCallbackData(deadlineThisYear);
        rowInline3.add(thisYear);


        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        rowsInline.add(rowInline1);
        rowsInline.add(rowInline2);
        rowsInline.add(rowInline3);
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);
    }

}
