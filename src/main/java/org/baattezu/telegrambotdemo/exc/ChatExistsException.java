package org.baattezu.telegrambotdemo.exc;


import org.baattezu.telegrambotdemo.repository.ChatRepository;

public class ChatExistsException extends RuntimeException{
    public ChatExistsException(String message){
        super(message);
    }
}
