package org.baattezu.telegrambotdemo.bot.scheduled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baattezu.telegrambotdemo.bot.GoalBot;
import org.baattezu.telegrambotdemo.model.GroupChat;
import org.baattezu.telegrambotdemo.model.User;
import org.baattezu.telegrambotdemo.results.ProgressImageGenerator;
import org.baattezu.telegrambotdemo.results.ScheduledResultsGenerator;
import org.baattezu.telegrambotdemo.service.ChatService;
import org.baattezu.telegrambotdemo.service.UserService;
import org.baattezu.telegrambotdemo.utils.BotMessagesEnum;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class GroupMessageScheduler {

    private final GoalBot telegramBot;
    private final ChatService chatService;
    private final UserService userService;

    private final ProgressImageGenerator progressImageGenerator;
    private final ScheduledResultsGenerator top5UsersResultsGenerator;


    // Отправка сообщений по субботам в 10:00
    @Scheduled(cron = "0 0 9 * * 6")
    public void sendSaturdayMessagesToGroups() {
        sendMessages(BotMessagesEnum.SCHEDULED_SATURDAY_MESSAGE.getMessage());
    }

    @Scheduled(cron = "05 18 23 * * 6")
    public void generateSaturdayResultsToGroups() {
        var gChats = chatService.getAllChats();
        for (GroupChat gc : gChats) {
            sendMessages(progressImageGenerator.formatTable(String.valueOf(gc.getId()), true));
        }
    }

    @Scheduled(cron = "05 50 23 * * 6")
    public void deleteWeekResultsToGroups() {
        var gChats = chatService.getAllChats();
        for (GroupChat gc : gChats) {
            progressImageGenerator.deleteResults(gc.getId().toString());
        }
    }

    // Желание успехов каждое утро в 9:00
    @Scheduled(cron = "0 0 9 * * *")
    public void sendMorningSuccessMessages() {
        sendMessages("🌟 Доброе утро! Успехов вам сегодня и продуктивного дня! 🌟");
    }
    @Scheduled(cron = "0 0 12 * * *")
    public void sendDailyMotivation() {
        List<String> quotes = List.of(
                "🌟 Верь в себя и иди вперед! 🌟",
                "💪 Каждый день — это новая возможность стать лучше! 💪",
                "🔥 Не жди чуда, твори его сам! 🔥",
                "🚀 Успех — это движение от неудачи к неудаче без потери энтузиазма! 🚀"
        );
        String randomQuote = quotes.get(new Random().nextInt(quotes.size()));
        sendMessages(randomQuote + "\nУспехов вам сегодня!");
    }

    @Scheduled(cron = "0 0 12 * * 3")  // Среда, 12:00
    public void sendMidweekProgressReminder() {
        sendMessages("🔄 Середина недели! Проверьте свои цели и скорректируйте план на оставшиеся дни. " +
                "Успех приходит к тем, кто действует! 💪");
    }

    @Scheduled(cron = "0 0 17 * * 5")  // Пятница, 17:00
    public void sendEndOfWeekReminder() {
        sendMessages("⏳ Неделя подходит к концу! Осталось немного времени для завершения ваших задач. " +
                "Сделайте последние рывки и завершите неделю на позитивной ноте! 💥");
    }

    // Запись целей на следующую неделю по воскресеньям
    @Scheduled(cron = "0 0 10 * * 7")
    public void sendWeeklyGoalReminder() {
        sendMessages("📝 Время записать цели на следующую неделю! " +
                "Какие задачи вы хотите выполнить? Поделитесь своими целями! 💪");
    }

    // Подведение итогов за неделю по субботам
    @Scheduled(cron = "0 0 18 * * 6")
    public void sendWeeklyResultsReminder() {
        sendMessages("📊 Пришло время подвести итоги за неделю! Как продвигается ваш прогресс? " +
                "Поделитесь своими результатами, чтобы вдохновить остальных! 🌟");
    }

    @Scheduled(cron = "0 0 10 1 * ?")  // Первое число каждого месяца
    public void sendSmallStepsReminder() {
        sendMessages("🚶‍♂️ Важно делать маленькие шаги каждый день. Большие успехи начинаются с малого, " +
                "не забывайте ставить реалистичные цели и двигаться к ним шаг за шагом! 🏆");
    }
    @Scheduled(cron = "0 0 10 * * 4")  // Четверг, 10:00
    public void sendFeedbackRequest() {
        sendMessages("📝 Мы хотим услышать ваше мнение! Как вам наша работа и бот? " +
                "Что можно улучшить? Оставьте отзыв или предложения, мы всегда рады вашему мнению! 💬");
    }

    // Отправка сообщений по воскресеньям в 11:00
    @Scheduled(cron = "0 0 10 * * 7")
    public void sendSundayMessagesToGroups() {
        sendResultsMessages();
    }

    private void sendResultsMessages() {
        List<String> groupChatIds = getGroupChatIds();

        for (String chatId : groupChatIds) {

            SendMessage groupResults1 = new SendMessage();
            groupResults1.setChatId(chatId);
            groupResults1.setText("\uD83C\uDF8A\uD83C\uDF8A\uD83C\uDF8A\uD83C\uDF8A\uD83C" +
                    "\uDF8A\uD83C\uDF8A\uD83C\uDF8A\uD83C\uDF8A\uD83C\uDF8A\uD83C\uDF8A\uD83C" +
                    "\uDF8A\uD83C\uDF8A\uD83C\uDF8A\uD83C\uDF8A");
            groupResults1.enableMarkdown(true);

            SendMessage groupResults = new SendMessage();
            groupResults.setChatId(chatId);
            groupResults.setText(top5UsersResultsGenerator.getTop5UsersInGroupMessage(chatId));
            groupResults.enableMarkdown(true);

            SendMessage top5GoalsAndResults = new SendMessage();
            top5GoalsAndResults.setChatId(chatId);
            top5GoalsAndResults.setText(top5UsersResultsGenerator.getTop5UsersGoalsAndResults(chatId));
            top5GoalsAndResults.enableHtml(true);

            try {
                telegramBot.execute(groupResults1);
                telegramBot.execute(groupResults);
                telegramBot.execute(top5GoalsAndResults);
            } catch (TelegramApiException e) {
                e.printStackTrace(); // Обработка ошибок
            }
        }
    }

    private void sendMessages(String messageText) {
        List<String> groupChatIds = getGroupChatIds();

        for (String chatId : groupChatIds) {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(messageText);

            try {
                telegramBot.execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace(); // Обработка ошибок
            }
        }
    }

    private List<String> getGroupChatIds() {
        var chats = chatService.getAllChats();
        return chats.stream().map(gc -> String.valueOf(gc.getId())).collect(Collectors.toList());
    }
}