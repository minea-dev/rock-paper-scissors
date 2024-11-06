package org.rockpaperscissors.backend.service;

import org.rockpaperscissors.backend.model.Game;
import org.rockpaperscissors.backend.model.GameMode;
import org.rockpaperscissors.backend.model.User;
import org.rockpaperscissors.backend.repository.GameModeRepository;
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
    private GameModeRepository gameModeRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Game> findAll() {
        return gameRepository.findAll();
    }

    public Optional<Game> findById(Long id) {
        return gameRepository.findById(id);
    }

    public List<Game> findByPlayer1Id(Long playerId) {
        return gameRepository.findByPlayer1Id(playerId);
    }

    public List<Game> findByPlayer2Id(Long playerId) {
        return gameRepository.findByPlayer2Id(playerId);
    }

    public List<Game> findByModeId(Long modeId) {
        return gameRepository.findByModeId(modeId);
    }

    public List<Game> findByIsRealTime(boolean isRealTime) {
        return gameRepository.findByIsRealTime(isRealTime);
    }

    public Game save(Game game) {
        return gameRepository.save(game);
    }

    // Start a new game based on player IDs, mode, rounds, and real-time status
    public Game startNewGame(Long player1Id, Long player2Id, String mode, int rounds, boolean isRealTime) {
        // Check if players exist in the database
        Optional<User> player1 = userRepository.findById(player1Id);
        Optional<User> player2 = player2Id != null ? userRepository.findById(player2Id) : Optional.empty();

        if (player1.isEmpty() || (player2Id != null && player2.isEmpty())) {
            throw new IllegalArgumentException("Invalid player IDs.");
        }

        // Fetch game mode by name
        GameMode gameMode = gameModeRepository.findByModeName(mode);

        // Create a new Game object
        Game newGame = new Game();
        newGame.setPlayer1(player1.get());
        newGame.setPlayer2(player2.orElse(null));
        newGame.setMode(gameMode);
        newGame.setRounds(rounds);
        newGame.setIsRealTime(isRealTime);
        newGame.setResult(Game.GameResult.ONGOING); // Initial status

        // Save and return the game
        return gameRepository.save(newGame);
    }
}

