package org.baattezu.telegrambotdemo.bot.callbacks.goal_creation;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baattezu.telegrambotdemo.bot.callbacks.Callback;
import org.baattezu.telegrambotdemo.bot.callbacks.CallbackHandler;
import org.baattezu.telegrambotdemo.data.UserGoalData;
import org.baattezu.telegrambotdemo.data.UserState;
import org.baattezu.telegrambotdemo.model.Goal;
import org.baattezu.telegrambotdemo.service.GoalService;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;

@Component
@Slf4j
@RequiredArgsConstructor
public class EnterDeadlineCallback implements CallbackHandler {
    private final GoalService goalService;


    @Override
    public SendMessage execute(Callback callback, Update update) {
        SendMessage responseMessage = null;
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Long userId = update.getCallbackQuery().getFrom().getId();
        UserGoalData data = goalService.getUserState(userId);
        String callbackData = callback.getData();
        if (data != null){
            Goal goal = goalService.getGoalById(data.goalId());
            String goalSummary = null;
            switch (callbackData) {
                case "deadline_today":
                    // Устанавливаем дедлайн на сегодня
                    log.info("Ставим дедл сегодня");
                    LocalDateTime today = LocalDateTime.now().withHour(23).withMinute(59);  // до конца дня
                    goalService.setGoalDeadline(goal, today);
                    goalService.clearUserState(userId);
                    responseMessage = getGoalSummary(goal, chatId);
                    break;
                case "deadline_tomorrow":
                    // Устанавливаем дедлайн на сегодня
                    LocalDateTime tomorrow = LocalDateTime.now().plusDays(1).withHour(23).withMinute(59);  // до конца дня
                    goalService.setGoalDeadline(goal, tomorrow);
                    goalService.clearUserState(userId);
                    responseMessage = getGoalSummary(goal, chatId);
                    break;

                case "deadline_this_week":
                    // Устанавливаем дедлайн на конец этой недели
                    LocalDateTime endOfWeek = LocalDateTime.now().with(ChronoField.DAY_OF_WEEK, 7).withHour(23).withMinute(59);
                    goalService.setGoalDeadline(goal, endOfWeek);
                    goalService.clearUserState(userId);
                    responseMessage = getGoalSummary(goal, chatId);
                    break;

                case "deadline_next_week":
                    // Устанавливаем дедлайн на конец этой недели
                    LocalDateTime nextWeek = LocalDateTime.now().with(ChronoField.DAY_OF_WEEK, 7).plusWeeks(1).withHour(23).withMinute(59);
                    goalService.setGoalDeadline(goal, nextWeek);
                    goalService.clearUserState(userId);
                    responseMessage = getGoalSummary(goal, chatId);
                    break;

                case "deadline_this_month":
                    // Устанавливаем дедлайн на конец этого месяца
                    LocalDateTime endOfMonth = LocalDateTime.now().with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59);
                    goalService.setGoalDeadline(goal, endOfMonth);
                    goalService.clearUserState(userId);
                    responseMessage = getGoalSummary(goal, chatId);
                    break;

                case "deadline_this_year":
                    // Устанавливаем дедлайн на конец года
                    LocalDateTime endOfYear = LocalDateTime.now().with(TemporalAdjusters.lastDayOfYear()).withHour(23).withMinute(59);
                    goalService.setGoalDeadline(goal, endOfYear);
                    goalService.clearUserState(userId);
                    responseMessage = getGoalSummary(goal, chatId);
                    break;

                default:
                    responseMessage = new SendMessage(String.valueOf(chatId), "Неизвестная команда.");
                    break;
            }
        }


        return responseMessage;
    }

    private static SendMessage getGoalSummary(Goal goal, Long chatId) {
        String goalSummary;
        SendMessage responseMessage;
        var formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        goalSummary = String.format("Цель успешно создана!\nНазвание: %s\nОписание: %s\nДедлайн: %s",
                goal.getGoalName(), goal.getDescription(), goal.getDeadline().format(formatter));

        responseMessage = new SendMessage(String.valueOf(chatId), goalSummary);
        return responseMessage;
    }
}
