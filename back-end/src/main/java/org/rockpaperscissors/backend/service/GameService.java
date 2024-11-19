package org.rockpaperscissors.backend.service;

import org.rockpaperscissors.backend.model.Game;
import org.rockpaperscissors.backend.repository.GameRepository;
import org.rockpaperscissors.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Game> findAll() {
        return gameRepository.findAll();
    }

    public Game findById(Long id) {
        return gameRepository.findById(id).orElse(null);
    }

    public List<Game> findByPlayerId(Long playerId) {
        return gameRepository.findByPlayer1Id(playerId);
    }

    public List<Game> findByPlayer2Id(Long playerId) {
        return gameRepository.findByPlayer2Id(playerId);
    }

    public List<Game> findByIsRealTime(boolean isRealTime) {
        return gameRepository.findByIsRealTime(isRealTime);
    }

    public void save(Game game) {
        gameRepository.save(game);
    }

}
