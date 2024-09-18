package org.baattezu.telegrambotdemo.repository;

import org.baattezu.telegrambotdemo.model.Goal;
import org.baattezu.telegrambotdemo.model.GroupChat;
import org.baattezu.telegrambotdemo.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByUserIdAndCompletedFalse(Long userId);
    List<Goal> findByCompletedFalse();
    List<Goal> findByUserId(Long userId);
    void deleteAllByUser(User user);

    @Query("SELECT g.user.id, COUNT(g.id) AS totalGoals, SUM(CASE WHEN g.completed = true THEN 1 ELSE 0 END) AS completedGoals " +
            "FROM Goal g WHERE g.user.groupChat = :groupChat GROUP BY g.user.id ORDER BY (SUM(CASE WHEN g.completed = true THEN 1 ELSE 0 END) / COUNT(g.id) * 100) DESC")
    List<Object[]> findTop5UsersWithCompletionRate(GroupChat groupChat, Pageable pageable);

}

