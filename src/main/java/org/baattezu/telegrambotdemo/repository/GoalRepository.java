package org.baattezu.telegrambotdemo.repository;

import org.baattezu.telegrambotdemo.model.Goal;
import org.baattezu.telegrambotdemo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByUserIdAndCompletedFalse(Long userId);
    List<Goal> findByCompletedFalse();
    List<Goal> findByUserId(Long userId);
    void deleteAllByUser(User user);
}

