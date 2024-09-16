package org.baattezu.telegrambotdemo.bot.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baattezu.telegrambotdemo.results.ResultGenerator;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetPhoto implements Command{
    private final ResultGenerator resultGenerator;
    @Override
    public SendMessage execute(Update update) {
        try{
            resultGenerator.generateResult(String.valueOf(update.getMessage().getChatId()));
        } catch (IOException e){
            log.error("trash");
        }
        return null;
    }
}
