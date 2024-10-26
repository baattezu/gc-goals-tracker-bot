package org.baattezu.telegrambotdemo.results;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baattezu.telegrambotdemo.model.User;
import org.baattezu.telegrambotdemo.service.ChatService;
import org.baattezu.telegrambotdemo.service.GoalService;
import org.baattezu.telegrambotdemo.service.UserService;

@org.springframework.stereotype.Component
@RequiredArgsConstructor
@Slf4j
public class ProgressImageGenerator {

    private final GoalService goalService;
    private final UserService userService;
    private final ChatService chatService;

    public void deleteResults(String chatId) {
        var chat = chatService.findById(Long.valueOf(chatId));
        var userList = userService.getAllUsersFromGroupChat(chat);

        for (User user : userList) {
            userService.clearResultsForWeek(user);
        }
        log.info("Результаты пользователей за неделю стерты.");
    }

    public String formatTable(String chatId, boolean isGroupMessage) {
        // Получаем данные для таблицы
        String[][] data = getData(chatId);

        // Формируем таблицу с использованием Markdown
        StringBuilder text = new StringBuilder();
        text.append("*Цели и результаты пользователей" +
                (!isGroupMessage ? " в вашей группе" : " в этой группе")
                + ":*\n");
        text.append("```\n");

        // Заголовок таблицы
        text.append(String.format("%-15s %-6s %-6s\n", "Имя", "Цели", "Рез-ы"));
        text.append(String.format("%-15s %-6s %-6s\n", "==============", "=====", "====="));

        for (int row = 1; row < data.length; row++) {
            // Проверяем, если имя превышает 20 символов, разбиваем его на несколько строк
            String username = data[row][0];
            if (username != null && username.length() > 15) {
                int splitIndex = 15;
                String part1 = username.substring(0, splitIndex);
                String part2 = username.substring(splitIndex);

                // Добавляем первую часть имени в таблицу
                text.append(String.format("%-15s %-5s %-5s\n", part1, data[row][1], data[row][2]));
                // Добавляем вторую часть имени в следующую строку с отступом
                if (part2.length() > 15) {
                    part2 = part2.substring(0, 12) + "..."; // Сокращаем до 7 символов + "..."
                }
                text.append(String.format("%-15s\n", part2));
            } else {
                // Если имя меньше 20 символов, просто добавляем его
                text.append(String.format("%-15s %-5s %-5s\n",
                        username != null ? username : "",
                        data[row][1] != null ? data[row][1] : "",
                        data[row][2] != null ? data[row][2] : ""));
            }
            text.append(String.format("%-15s %-6s %-6s\n", "--------------", "-----", "-----"));
        }
        text.append("```");

        return text.toString();
    }

    public String[][] getData(String chatId) {
        var chat = chatService.findById(Long.valueOf(chatId));
        var userList = userService.getAllUsersFromGroupChat(chat);
        String[][] data = new String[userList.size() + 1][3];
        data[0] = new String[]{"Имя", "Цели", "Результат"}; // Добавление заголовка
        int row = 1; // Начинаем с первой строки после заголовка
        for (User user : userList) {
            var username = user.getUsername();
            String statusGoals = goalService.isThereAnyGoals(user.getId())
                    ? "✅" : "❌";
            String statusResults = !user.getResultsForWeek().equals("Пока нет результатов")
                    ? "✅" : "❌";
            // Добавляем данные в массив
            data[row][0] = username;
            data[row][1] = statusGoals;
            data[row][2] = statusResults;
            row++; // Переход к следующей строке
        }
        return data;
    }


}