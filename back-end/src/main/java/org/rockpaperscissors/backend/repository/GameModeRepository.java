package org.rockpaperscissors.backend.repository;

import org.rockpaperscissors.backend.model.GameMode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameModeRepository extends JpaRepository<GameMode, Long> {
    GameMode findByModeName(String modeName);
}
