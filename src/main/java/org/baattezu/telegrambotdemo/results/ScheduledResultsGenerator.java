package org.baattezu.telegrambotdemo.results;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.baattezu.telegrambotdemo.model.Goal;
import org.baattezu.telegrambotdemo.model.GroupChat;
import org.baattezu.telegrambotdemo.model.User;
import org.baattezu.telegrambotdemo.service.ChatService;
import org.baattezu.telegrambotdemo.service.GoalService;
import org.baattezu.telegrambotdemo.service.UserService;
import org.baattezu.telegrambotdemo.utils.BotMessagesEnum;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramBot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ScheduledResultsGenerator{

    private final GoalService goalService;
    private final UserService userService;
    private final ChatService chatService;


    @Transactional
    public List<Map.Entry<User, Double>> getTop5UsersInGroup(String chatId){
        var chat = chatService.findById(Long.valueOf(chatId));
        var userList = userService.getAllUsersFromGroupChat(chat);
        var top5Users = goalService.getTop5UsersFromStreamAPI(userList);
        return new ArrayList<>(top5Users.entrySet());
    }

    @Transactional
    public String getTop5UsersInGroupMessage(String chatId){
        List<Map.Entry<User, Double>> topUsersList = getTop5UsersInGroup(chatId);

        StringBuilder announcement = new StringBuilder();
        announcement.append("✨ *Топ-5 самых результативных участников!* ✨\n\n")
                .append("Дорогие друзья, мы рады поделиться с вами достижениями нашей группы! 🌟 Благодаря вашему упорству и стремлению к успеху, ")
                .append("мы собрали топ-5 пользователей, которые показали наивысшие результаты за последнее время:\n\n");

        int rank = 1;
        String[] medals = {"🥇", "🥈", "🥉", "🏅", "🏅"};

        for (Map.Entry<User, Double> entry : topUsersList) {
            announcement.append(String.format("%s *%d место:* %s – %.2f%%\n",
                    medals[rank - 1],
                    rank,
                    entry.getKey().getUsername(),
                    entry.getValue()
            ));
            rank++;
        }

        announcement.append("\nВаше усердие и мотивация вдохновляют нас всех! 🎉 Продолжайте в том же духе, и вместе мы добьемся еще больших высот. ")
                .append("Не забывайте, что каждое маленькое усилие приближает вас к большой цели. Вы молодцы! 💪🌈\n\n")
                .append("Давайте продолжим поддерживать друг друга и стремиться к лучшему! 🚀")
                .append("Под этим сообщением вы можете найти кнопки для просмотра целей и результатов топ-5 пользователей. 🎯\n")
                .append("Выберите 1#, 2#, или 3# для детализации их достижений.\n");

        return announcement.toString();
    }


}
