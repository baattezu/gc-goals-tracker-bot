package org.baattezu.telegrambotdemo.service;

import lombok.RequiredArgsConstructor;
import org.baattezu.telegrambotdemo.data.UserState;
import org.baattezu.telegrambotdemo.model.User;
import org.baattezu.telegrambotdemo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Optional<User> findUserByChatId(Long chatId) {
        return userRepository.findByChatId(chatId);
    }

    public User registerUser(Long userId, String username, String name) {
        User user = userRepository.findById(userId).orElseGet(() -> {
            User newUser = new User();
            newUser.setId(userId);
            newUser.setUsername(username);
            newUser.setChatId(99999L);
            return newUser;
        });
        user.setUsername(name); // Сохраняем имя пользователя
        return userRepository.save(user);
    }

    public User findById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
