package org.rockpaperscissors.backend.controller;

import org.rockpaperscissors.backend.model.Game;
import org.rockpaperscissors.backend.model.Round;
import org.rockpaperscissors.backend.repository.GameModeRepository;
import org.rockpaperscissors.backend.repository.GameRepository;
import org.rockpaperscissors.backend.service.GameService;
import org.rockpaperscissors.backend.service.RoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class GameLogicService {

    @Autowired
    private RoundService roundService;
    @Autowired
    private GameService gameService;

    private final Random random = new Random();

    private static final List<String> CLASSIC_MOVES = List.of("ROCK", "PAPER", "SCISSORS");
    private static final List<String> SPOCK_MOVES = List.of("ROCK", "PAPER", "SCISSORS", "LIZARD", "SPOCK");


    // Round Result
    public String determineRoundResult(String player1Move, String player2Move, String mode) {

        if (player1Move.equals(player2Move)) {
            return "DRAW";
        }
        // true = player1 wins | false = player1 loses
        boolean result = mode.equalsIgnoreCase("CLASSIC") ? classicRules(player1Move, player2Move) : additionalSpockRules(player1Move, player2Move);

        return result ? "PLAYER1_WIN" : "PLAYER2_WIN";
    }

    // Game Result
    public String determineGameResult(Long gameId) {
        Game game = gameService.findById(gameId).orElseThrow(() ->
                new RuntimeException("Game no encontrado con id: " + gameId));

        // If the result is already set
        if (!game.getResult().toString().equals("ONGOING")) {
            return game.getResult().toString();
        }

        // If the result is not set yet
        List<Round> rounds = roundService.findByGameId(gameId);

        Map<Round.RoundResult, Long> resultCounts = rounds.stream()
                .map(Round::getResult)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        long player1Wins = resultCounts.getOrDefault(Round.RoundResult.PLAYER1_WIN, 0L);
        long player2Wins = resultCounts.getOrDefault(Round.RoundResult.PLAYER2_WIN, 0L);

        Game.GameResult finalResult;
        if (player1Wins > player2Wins) {
            finalResult = Game.GameResult.PLAYER1_WIN;
        } else if (player2Wins > player1Wins) {
            finalResult = Game.GameResult.PLAYER2_WIN;
        } else {
            finalResult = Game.GameResult.DRAW;
        }

        game.setResult(finalResult);

        gameService.save(game);

        return finalResult.toString();
    }

    // Computer as Player2
    public String generateRandomMove(String mode) {
        List<String> validMoves = mode.equalsIgnoreCase("CLASSIC") ? CLASSIC_MOVES : SPOCK_MOVES;
        return validMoves.get(random.nextInt(validMoves.size()));
    }

    // Classic logic
    private boolean classicRules(String player1Move, String player2Move) {
        return (player1Move.equals("ROCK") && player2Move.equals("SCISSORS")) ||
                (player1Move.equals("PAPER") && player2Move.equals("ROCK")) ||
                (player1Move.equals("SCISSORS") && player2Move.equals("PAPER"));
    }
    // Logic with Spock Rules
    private boolean additionalSpockRules(String player1Move, String player2Move) {
        return (player1Move.equals("ROCK") && player2Move.equals("LIZARD")) ||
                (player1Move.equals("LIZARD") && player2Move.equals("SPOCK")) ||
                (player1Move.equals("SPOCK") && player2Move.equals("SCISSORS")) ||
                (player1Move.equals("SCISSORS") && player2Move.equals("LIZARD")) ||
                (player1Move.equals("LIZARD") && player2Move.equals("PAPER")) ||
                (player1Move.equals("PAPER") && player2Move.equals("SPOCK")) ||
                (player1Move.equals("SPOCK") && player2Move.equals("ROCK"));
    }
}

