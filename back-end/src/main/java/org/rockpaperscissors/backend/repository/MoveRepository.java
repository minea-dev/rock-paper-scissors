package org.rockpaperscissors.backend.repository;

import org.rockpaperscissors.backend.model.Move;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoveRepository extends JpaRepository<Move, Long> {
    Move findByMoveName(String moveName);
}
