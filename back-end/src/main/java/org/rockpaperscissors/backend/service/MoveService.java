package org.rockpaperscissors.backend.service;

import org.rockpaperscissors.backend.model.Move;
import org.rockpaperscissors.backend.repository.MoveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class MoveService {

    @Autowired
    private MoveRepository moveRepository;

    public List<Move> findAll() {
        return moveRepository.findAll();
    }

    public Optional<Move> findById(Long id) {
        return moveRepository.findById(id);
    }

    public Optional<Move> findByMoveName(String moveName) {
        return Optional.ofNullable(moveRepository.findByMoveName(moveName));
    }

    public Move save(Move move) {
        return moveRepository.save(move);
    }

    // Convert the String to MoveName enum
    public Move convertStringToMove(String moveName) {
        Move move = new Move();
        Move.MoveName nameEnum = Move.MoveName.valueOf(moveName.toUpperCase());
        move.setMoveName(nameEnum);
        return move;
    }
}
