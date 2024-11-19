package org.rockpaperscissors.backend.repository;

import org.rockpaperscissors.backend.model.Round;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoundRepository extends JpaRepository<Round, Long> {
    List<Round> findByGameId(Long gameId);
    List<Round> findByGameIdAndRoundNumber(Long gameId, int roundNumber);
    @Query("SELECT MAX(r.roundNumber) FROM Round r WHERE r.game.id = :gameId")
    Optional<Integer> findMaxRoundNumberByGameId(@Param("gameId") Long gameId);
}
