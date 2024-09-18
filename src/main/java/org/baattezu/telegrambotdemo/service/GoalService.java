package org.baattezu.telegrambotdemo.service;

import lombok.RequiredArgsConstructor;
import org.baattezu.telegrambotdemo.data.UserGoalData;
import org.baattezu.telegrambotdemo.data.UserState;
import org.baattezu.telegrambotdemo.model.Goal;
import org.baattezu.telegrambotdemo.model.GroupChat;
import org.baattezu.telegrambotdemo.model.User;
import org.baattezu.telegrambotdemo.repository.GoalRepository;
import org.baattezu.telegrambotdemo.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    private final Map<Long, UserGoalData> userStates = new HashMap<>();

    public void setUserState(Long userId, UserState state, Long goalId) {
        UserGoalData userGoalData = new UserGoalData(state, goalId);
        userStates.put(userId, userGoalData);
    }
    public UserGoalData getUserState(Long userId) {
        return userStates.getOrDefault(userId, null);
    }
    public void clearUserState(Long userId) {
        userStates.remove(userId);
    }

    public List<Goal> getAllPendingGoalsForUser(Long userId) {
        return goalRepository.findByUserIdAndCompletedFalse(userId);
    }
    public List<Goal> getAllPendingGoals() {
        return goalRepository.findByCompletedFalse();
    }

    public void clearGoalsForWeek(User user){
        goalRepository.deleteAllByUser(user);
    }

    public Goal createBlankGoal(User user) {
        Goal goal = new Goal();
        goal.setUser(user);
        goal.setCompleted(false);
        goal.setGoalName("Пусто");
        goal.setDescription("Пусто");
        goal.setCreatedAt(LocalDateTime.now());
        goal.setDeadline(LocalDateTime.now().plusDays(1));
        return goalRepository.save(goal);
    }
    public Goal setGoalName(Goal goal, String name){
        goal.setGoalName(name);
        return goalRepository.save(goal);
    }
    public Goal setGoalDescription(Goal goal, String description){
        goal.setDescription(description);
        return goalRepository.save(goal);
    }
    public Goal setGoalDeadline(Goal goal, LocalDateTime deadline){
        goal.setDeadline(deadline);
        return goalRepository.save(goal);
    }
    public Goal setGoalReward(Goal goal, String reward){
        goal.setReward(reward);
        return goalRepository.save(goal);
    }

    public List<Goal> getAllGoals(Long userId, boolean pendingOrAll) {
        var goalList = pendingOrAll ?
                goalRepository.findByUserIdAndCompletedFalse(userId) :
                goalRepository.findByUserId(userId);
        goalList.sort(Comparator
                .comparing(Goal::getCreatedAt)              // Сначала по дате
                .thenComparing(Goal::getCompleted));
        return goalList;
    }

    public Goal getGoalById(Long goalId){
        return goalRepository.findById(goalId).orElse(null);
    }

    public void changeGoalCompletion(Goal goal) {
        goal.setCompleted(!goal.getCompleted());
        goalRepository.save(goal);
    }


    public Map<User, Double> getTop5UsersFromStreamAPI(List<User> users) {
        return users.stream()
                // Преобразуем пользователя в пару (пользователь, процент)
                .map(u -> {
                    var allGoalList = getAllGoals(u.getId(), false);
                    var completedGoals = allGoalList.stream().filter(Goal::getCompleted).toList();

                    long countCompletedGoals = completedGoals.size();
                    long countAllGoals = allGoalList.size();

                    // Избегаем деления на ноль
                    double percent = countAllGoals > 0 ? (double) countCompletedGoals / countAllGoals * 100 : 0;

                    // Возвращаем пару (пользователь, процент)
                    return new AbstractMap.SimpleEntry<>(u, percent);
                })
                // Сортируем по проценту выполнения в порядке убывания
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                // Берем топ-5
                .limit(5)
                // Собираем пары в отсортированную карту
                .collect(Collectors.toMap(
                        Map.Entry::getKey, // Ключ - пользователь
                        Map.Entry::getValue, // Значение - процент
                        (e1, e2) -> e1, // В случае дубликата оставляем первый элемент (не должно быть дубликатов)
                        LinkedHashMap::new // Используем LinkedHashMap для сохранения порядка
                ));
    }

    public Map<User, Double> getTop5UsersInGroupFromSQLQuery(GroupChat groupChat) {
        // Получаем топ-5 пользователей с процентами выполнения целей
        Pageable pageable = PageRequest.of(0, 5);
        List<Object[]> results = goalRepository.findTop5UsersWithCompletionRate(groupChat, pageable);

        // Преобразуем список результатов в карту пользователей и процентов
        return results.stream()
                .map(result -> {
                    Long userId = (Long) result[0];
                    double percent = ((Number) result[1]).doubleValue();
                    User user = userRepository.findById(userId).orElse(null);
                    return new AbstractMap.SimpleEntry<>(user, percent);
                })
                .filter(entry -> entry.getKey() != null)
                // Собираем пары в Map
                .collect(Collectors.toMap(
                        Map.Entry::getKey, // Ключ - пользователь
                        Map.Entry::getValue, // Значение - процент
                        (e1, e2) -> e1, // В случае дубликата оставляем первый элемент (не должно быть дубликатов)
                        LinkedHashMap::new // Используем LinkedHashMap для сохранения порядка
                ));
    }
}