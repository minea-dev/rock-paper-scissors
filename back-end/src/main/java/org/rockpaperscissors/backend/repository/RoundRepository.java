package org.rockpaperscissors.backend.repository;

import org.rockpaperscissors.backend.model.Round;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoundRepository extends JpaRepository<Round, Long> {
    List<Round> findByGameId(Long gameId);
    List<Round> findByGameIdAndRoundNumber(Long gameId, int roundNumber);
}
