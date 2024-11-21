package org.rockpaperscissors.backend.service;

import org.rockpaperscissors.backend.model.Game;
import org.rockpaperscissors.backend.repository.GameRepository;
import org.rockpaperscissors.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        List<Game> player1Games = gameRepository.findByPlayer1Id(playerId);
        List<Game> player2Games = gameRepository.findByPlayer2Id(playerId);

        List<Game> allGames = player1Games.stream()
                .collect(Collectors.toList());
        allGames.addAll(player2Games);

        return allGames;
    }

    public void save(Game game) {
        gameRepository.save(game);
    }

}
