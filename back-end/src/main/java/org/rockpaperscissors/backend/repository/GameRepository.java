package org.rockpaperscissors.backend.repository;

import org.rockpaperscissors.backend.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {
    Game findById(long id);
    List<Game> findByPlayer1Id(Long playerId);
    List<Game> findByPlayer2Id(Long playerId);

    List<Game> findByModeId(Long modeId);

    List<Game> findByIsRealTime(boolean isRealTime);
}
