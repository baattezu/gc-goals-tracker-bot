package org.baattezu.telegrambotdemo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baattezu.telegrambotdemo.data.UserGoalData;
import org.baattezu.telegrambotdemo.data.UserState;
import org.baattezu.telegrambotdemo.model.Goal;
import org.baattezu.telegrambotdemo.model.User;
import org.baattezu.telegrambotdemo.repository.GoalRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalService {

    private final GoalRepository goalRepository;
    private final Map<Long, UserGoalData> userStates = new HashMap<>();

    public void setUserState(Long userId, UserState state, Long goalId) {
        UserGoalData currentData = userStates.getOrDefault(userId, null);
        // Если пользователь уже находится в определенном состоянии, можно отклонить изменение
        if (currentData != null && currentData.userState() != state) {
            // Логика отказа или предупреждение пользователю
            throw new RuntimeException("Смена состояний недопустима");
        }
        UserGoalData userGoalData = new UserGoalData(state, goalId);
        userStates.put(userId, userGoalData);
    }

    public UserGoalData getUserState(Long userId) {
        return userStates.getOrDefault(userId, null);
    }

    public void clearUserState(Long userId) {
        userStates.remove(userId);
    }

    public void clearGoalsForWeek(User user) {
        goalRepository.deleteAllByUser(user);
    }

    public Goal createBlankGoal(Long userId) {
        Goal goal = new Goal();
        goal.setUserId(userId);
        goal.setCompleted(false);
        goal.setGoal("Пусто");
        goal.setCreatedAt(LocalDateTime.now());
        goal.setDeadline(LocalDateTime.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)));
        return goalRepository.save(goal);
    }

    public void setGoal(Goal goal, String name) {
        goal.setGoal(name);
        goalRepository.save(goal);
    }

    public void setGoalDeadline(Goal goal, LocalDateTime deadline) {
        goal.setDeadline(deadline);
        goalRepository.save(goal);
    }

    public void setGoalReward(Goal goal, String reward) {
        goal.setReward(reward);
        goalRepository.save(goal);
    }

    public List<Goal> getAllGoals(Long userId) {
        var goalList = goalRepository.findByUserIdAndDeadlineForCurrentWeek(userId);
        goalList.sort(Comparator
                .comparing(Goal::getCreatedAt)              // Сначала по дате
                .thenComparing(Goal::getCompleted));
        return goalList;
    }

    public Goal getGoalById(Long goalId) {
        if (goalId == null) {
            return null;
        }
        return goalRepository.findById(goalId).orElse(null);
    }

    public void changeGoalCompletion(Goal goal) {
        goal.setCompleted(!goal.getCompleted());
        goalRepository.save(goal);
    }

    public boolean isThereAnyGoals(Long userId) {
        var goals = getAllGoals(userId);
        return !goals.isEmpty();
    }


    public Map<User, Double> getTop5UsersFromStreamAPI(List<User> users) {
        return users.stream()
                // Преобразуем пользователя в пару (пользователь, процент)
                .map(u -> {
                    var allGoalList = getAllGoals(u.getId());
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
}