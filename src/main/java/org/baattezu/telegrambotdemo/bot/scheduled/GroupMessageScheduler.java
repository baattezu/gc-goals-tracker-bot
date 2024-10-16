package org.baattezu.telegrambotdemo.bot.scheduled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baattezu.telegrambotdemo.bot.GoalBot;
import org.baattezu.telegrambotdemo.model.GroupChat;
import org.baattezu.telegrambotdemo.results.ProgressImageGenerator;
import org.baattezu.telegrambotdemo.results.ScheduledResultsGenerator;
import org.baattezu.telegrambotdemo.service.ChatService;
import org.baattezu.telegrambotdemo.utils.BotMessagesEnum;
import org.baattezu.telegrambotdemo.utils.TelegramBotHelper;
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
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class GroupMessageScheduler {

    private final static String RESULTS_PATH = "/results/resultsimagefolder";
    private final GoalBot telegramBot;
    private final ChatService chatService;

    private final ProgressImageGenerator progressImageGenerator;
    private final ScheduledResultsGenerator top5UsersResultsGenerator;


    // Отправка сообщений каждый день в 9:00
    // Отправка сообщений каждый день в 15:00 (3 часа дня)
//    @Scheduled(cron = "0 10 17 * * ?")
//    public void sendDailyMessagesToGroups() {
//        sendMessages(BotMessagesEnum.);
//    }

    // Отправка сообщений по субботам в 10:00
    @Scheduled(cron = "0 0 9 * * 6")
    public void sendSaturdayMessagesToGroups() {
        sendMessages(BotMessagesEnum.SCHEDULED_SATURDAY_MESSAGE.getMessage());
    }

    @Scheduled(cron = "05 18 23 * * 6")
    public void generateSaturdayResultsToGroups() {
        var gChats = chatService.getAllChats();
        for (GroupChat gc : gChats){
            try {
                progressImageGenerator.generateResult(String.valueOf(gc.getId()));
                sendMessages(progressImageGenerator.formatTable(String.valueOf(gc.getId()), true));
            } catch (IOException e) {
                log.error("Не удалось создать таблицу для чата " + gc.getName() + " ;(");
                throw new RuntimeException(e);
            }
        }
    }
    @Scheduled(cron = "05 50 23 * * 6")
    public void deleteWeekResultsToGroups() {
        var gChats = chatService.getAllChats();
        for (GroupChat gc : gChats){
            try {
                progressImageGenerator.generateResult(String.valueOf(gc.getId()));
            } catch (IOException e) {
                log.error("Не удалось создать таблицу для чата " + gc.getName() + " ;(");
                throw new RuntimeException(e);
            }
        }
    }
    @Scheduled(cron = "0 7 20 * * 7")
    public void sendSaturdayResultsToGroups() {

        var gChats = chatService.getAllChats();
        List<SendPhoto> sendPhotoRequests = new ArrayList<>();
        for (GroupChat gc : gChats){
            SendPhoto sendPhotoRequest = new SendPhoto();
            sendPhotoRequest.setChatId(gc.getId());
            sendPhotoRequest.setPhoto(new InputFile(Paths.get(RESULTS_PATH, gc.getId()+".png").toFile()));
        }
        try {
            for (var sendPhotoRequest : sendPhotoRequests){
                telegramBot.execute(sendPhotoRequest);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace(); // Обработка ошибок
        }
    }
    // Отправка сообщений по воскресеньям в 11:00
    @Scheduled(cron = "0 32 20 * * *")
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