package org.baattezu.telegrambotdemo.bot.commands.results;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baattezu.telegrambotdemo.bot.commands.Command;
import org.baattezu.telegrambotdemo.results.ProgressImageGenerator;
import org.baattezu.telegrambotdemo.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetPhoto implements Command {
    private final UserService userService;
    private final ProgressImageGenerator progressImageGenerator;

    @Override
    public SendMessage execute(Update update) {
        var message = update.getMessage();
        var isGroupMessage = message.isGroupMessage();
        var sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        if (!isGroupMessage){
            var user = userService.findById(message.getFrom().getId());
            sendMessage.setText(formatTable(user.getGroupChat().getId().toString(), false));
        } else {
            sendMessage.setText(formatTable(message.getChatId().toString(), true));
        }
        sendMessage.enableMarkdown(true);
        return sendMessage;
    }

    private String formatTable(String chatId, boolean isGroupMessage) {
        // Получаем данные для таблицы
        String[][] data = progressImageGenerator.getData(chatId);
        log.info(chatId);

        // Формируем таблицу с использованием Markdown
        StringBuilder text = new StringBuilder();
        text.append("*Цели и результаты пользователей" +
                (!isGroupMessage ? " в вашей группе": " в этой группе")
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


}
