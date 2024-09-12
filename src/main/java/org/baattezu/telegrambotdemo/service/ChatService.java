package org.baattezu.telegrambotdemo.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.baattezu.telegrambotdemo.exc.ChatExistsException;
import org.baattezu.telegrambotdemo.model.Chat;
import org.baattezu.telegrambotdemo.model.User;
import org.baattezu.telegrambotdemo.repository.ChatRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;

    private Set<Long> groupChatIds;

    @PostConstruct
    public void init() {
        List<Chat> chatList = chatRepository.findAll();
        groupChatIds = chatList.stream().map(Chat::getId).collect(Collectors.toSet());
    }
    public Chat findById(Long id){
        return chatRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Such group not included in supported groups.")
        );
    }

    public boolean isChatExists(Long chatId) {
        return groupChatIds.contains(chatId);
    }
    private void addGroupChatToTempStorage(Long chatId){
        groupChatIds.add(chatId);
    }
    public Chat createGroupChat(Long id, String chatName){
        Chat chat = new Chat();
        chat.setId(id);
        chat.setName(chatName);
        groupChatIds.add(chat.getId());
        return chatRepository.save(chat);
    }
    public List<Chat> getAllChats(){
        return chatRepository.findAll();
    }
}
