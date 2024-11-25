package org.rockpaperscissors.backend.service;

import org.rockpaperscissors.backend.model.Game;
import org.rockpaperscissors.backend.repository.GameRepository;
import org.rockpaperscissors.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    public Game findById(Long id) {
        return gameRepository.findById(id).orElse(null);
    }

    public List<Game> findByPlayerId(Long playerId) {
        List<Game> player1Games = gameRepository.findByPlayer1Id(playerId);
        List<Game> player2Games = gameRepository.findByPlayer2Id(playerId);
        return Stream.concat(player1Games.stream(), player2Games.stream())
                .sorted(Comparator.comparing(Game::getCreatedAt))
                .collect(Collectors.toList());
    }

    public void save(Game game) {
        gameRepository.save(game);
    }
}
