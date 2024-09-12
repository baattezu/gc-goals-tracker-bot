package org.baattezu.telegrambotdemo.bot.callbacks;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.baattezu.telegrambotdemo.data.CallbackType;

@Data
@AllArgsConstructor
@Builder
public class Callback {

    private CallbackType callbackType;

    private String data;

}