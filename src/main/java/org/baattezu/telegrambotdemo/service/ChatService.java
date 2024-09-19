package org.baattezu.telegrambotdemo.service;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.baattezu.telegrambotdemo.model.GroupChat;
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
    @Transactional
    public void init() {
        List<GroupChat> chatList = chatRepository.findAll();
        groupChatIds = chatList.stream().map(GroupChat::getId).collect(Collectors.toSet());
    }
    public GroupChat findById(Long id){
        return chatRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Such group not included in supported groups.")
        );
    }

    public boolean isChatExists(Long chatId) {
        return groupChatIds.contains(chatId);
    }

    public GroupChat createGroupChat(Long id, String chatName){
        GroupChat chat = new GroupChat();
        chat.setId(id);
        chat.setName(chatName);
        groupChatIds.add(chat.getId());
        return chatRepository.save(chat);
    }
    public List<GroupChat> getAllChats(){
        return chatRepository.findAll();
    }
}
