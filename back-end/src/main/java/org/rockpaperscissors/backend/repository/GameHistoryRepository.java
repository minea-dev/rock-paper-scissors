package org.rockpaperscissors.backend.repository;

import org.rockpaperscissors.backend.model.GameHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameHistoryRepository extends JpaRepository<GameHistory, Long> {
    List<GameHistory> findByUserId(Long userId);

    List<GameHistory> findByGameId(Long gameId);

    List<GameHistory> findByUserIdAndResult(String result);
}
