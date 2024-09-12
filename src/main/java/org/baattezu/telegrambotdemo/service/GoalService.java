package org.baattezu.telegrambotdemo.service;

import lombok.RequiredArgsConstructor;
import org.baattezu.telegrambotdemo.data.UserGoalData;
import org.baattezu.telegrambotdemo.data.UserState;
import org.baattezu.telegrambotdemo.model.Goal;
import org.baattezu.telegrambotdemo.model.User;
import org.baattezu.telegrambotdemo.repository.GoalRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;

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

    public List<Goal> getPendingGoals(User user) {
        return goalRepository.findByUserIdAndCompletedFalse(user.getId());
    }
    public List<Goal> getAllGoals(User user) {
        return goalRepository.findByUserId(user.getId());
    }
    public Goal getGoalById(Long goalId){
        return goalRepository.findById(goalId).orElseThrow(
                () -> new RuntimeException("Goal not found")
        );
    }

    public void changeGoalCompletion(Goal goal) {
        goal.setCompleted(!goal.getCompleted());
        goalRepository.save(goal);
    }

}