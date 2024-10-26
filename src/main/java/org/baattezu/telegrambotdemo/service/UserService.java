package org.baattezu.telegrambotdemo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baattezu.telegrambotdemo.model.GroupChat;
import org.baattezu.telegrambotdemo.model.User;
import org.baattezu.telegrambotdemo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public boolean isRegistered(Long userId) {
        return userRepository.existsById(userId);
    }

    public User registerUser(Long userId, String username, Long privateChatId) {
        var newUser = new User();
        newUser.setId(userId);
        newUser.setUsername(username);
        newUser.setPrivateChatId(privateChatId);
        newUser.setResultsForWeek("Пока нет результатов");
        newUser.setGroupChat(null);
        return userRepository.save(newUser);
    }

    @Transactional
    public User changeName(Long userId, String newName) {
        var user = findById(userId);
        user.setUsername(newName);
        return userRepository.save(user);
    }

    public User pinToChat(User user, GroupChat chat) {
        user.setGroupChat(chat);
        return userRepository.save(user);
    }

    public User findById(Long id) {
        // Метод обращается к базе данных для получения пользователя по ID
        return userRepository.findById(id).orElse(null);
    }

    public void clearResultsForWeek(User user) {
        user.setResultsForWeek("Пока нет результатов");
        userRepository.save(user);
    }

    public void writeResultsForWeek(Long userId, String results) {
        var user = findById(userId);
        user.setResultsForWeek(results);
        userRepository.save(user);
    }

    public Long getPrivateChatId(Long userId) {
        return findById(userId).getPrivateChatId();
    }

    public List<User> getAllUsersFromGroupChat(GroupChat chat) {
        return userRepository.findAllByGroupChat(chat);
    }


}
