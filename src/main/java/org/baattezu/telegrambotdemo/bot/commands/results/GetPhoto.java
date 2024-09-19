package org.baattezu.telegrambotdemo.bot.commands.results;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baattezu.telegrambotdemo.bot.commands.Command;
import org.baattezu.telegrambotdemo.results.ProgressImageGenerator;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetPhoto implements Command {
    private final ProgressImageGenerator progressImageGenerator;
    @Override
    public SendMessage execute(Update update) {
        try{
            progressImageGenerator.generateResult(String.valueOf(update.getMessage().getChatId()));
        } catch (IOException e){
            log.error("trash");
        }
        return null;
    }
}
