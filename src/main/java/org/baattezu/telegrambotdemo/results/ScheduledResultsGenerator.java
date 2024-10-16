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

        int rank = 1;
        String[] medals = {"🥇", "🥈", "🥉", "🏅", "🏅"};
        StringBuilder topUsersBuilder = new StringBuilder();

        for (Map.Entry<User, Double> entry : topUsersList) {
            topUsersBuilder.append(String.format("%s *%d место:* %s – %.2f%%\n",
                    medals[rank - 1],
                    rank,
                    entry.getKey().getUsername(),
                    entry.getValue()
            ));
            rank++;
        }

        // Подстановка значений в шаблон
        return BotMessagesEnum.SCHEDULED_SATURDAY_RESULTS.getMessage(topUsersBuilder.toString());
    }
    @Transactional
    public String getTop5UsersGoalsAndResults(String chatId){
        List<Map.Entry<User, Double>> topUsersList = getTop5UsersInGroup(chatId);

        StringBuilder text = new StringBuilder();
        var rank = 1;
        for (Map.Entry<User, Double> entry : topUsersList) {
            var user = entry.getKey();
            var goals = goalService.getAllGoals(user.getId(), false);
            var newText = new StringBuilder("\n" +
                    "Цели и результаты " + rank + "#:\n");
            var index = 1;
            for (var g : goals){
                newText.append(BotMessagesEnum.GET_GOAL_DETAIL_MESSAGE_VER1.getMessage(
                        index,  g.getCompleted() ? "✅" : "", g.getGoal(), g.getReward()));
                index++;
            }
            text.append(newText).append("\n\n______________________");
            rank++;
        }
        if (text.toString().contains("Цели и результаты")){
            text = new StringBuilder(text.toString().replace("Цели и результаты", "<blockquote expandable> Цели и результаты"));
            text = new StringBuilder(text.toString().replace("______________________", "______________________</blockquote>"));
        }
        return text.toString();
    }




}
