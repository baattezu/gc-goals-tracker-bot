package org.baattezu.telegrambotdemo.bot;

//import lombok.RequiredArgsConstructor;
//import org.baattezu.telegrambotdemo.model.Goal;
//import org.baattezu.telegrambotdemo.service.GoalService;
//import org.baattezu.telegrambotdemo.service.UserService;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.telegram.telegrambots.bots.TelegramLongPollingBot;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import org.baattezu.telegrambotdemo.model.User;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//public class BotScheduler {
//
//    private final GoalService goalService;
//    private final UserService userService;
//    private final GoalBot goalBot;
//
//
//    @Scheduled(cron = "0 0 9 * * ?")  // Каждый день в 9 утра
//    public void sendReminders() {
//        List<User> users = userService.getAllUsers();
//        for (User user : users) {
//            SendMessage message = new SendMessage();
//            message.setChatId(user.getChatId().toString());
//            message.setText("Доброе утро! Не забудьте проверить свои цели на сегодня." + " \uD83D\uDE3C");
//            try {
//                execute()
//            } catch (TelegramApiException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//}