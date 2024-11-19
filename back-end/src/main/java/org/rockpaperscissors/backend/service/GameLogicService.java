package org.rockpaperscissors.backend.service;

import org.rockpaperscissors.backend.model.Game;
import org.rockpaperscissors.backend.model.Round;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    public Game.GameResult determineRoundResult(String player1Move, String player2Move, String mode) {
        if (player1Move.equals("NONE") || player2Move.equals("NONE")) {
            return Game.GameResult.CONFIGURED;
        }
        if (player1Move.equals(player2Move)) {
            return Game.GameResult.DRAW;
        }
        boolean result = mode.equalsIgnoreCase("CLASSIC") ? classicRules(player1Move, player2Move) : additionalSpockRules(player1Move, player2Move);
        return result ? Game.GameResult.PLAYER1_WIN : Game.GameResult.PLAYER2_WIN;
    }

    public String determineGameResult(Long gameId) {
        Optional<Game> optionalGame = Optional.ofNullable(gameService.findById(gameId));
        if (optionalGame.isEmpty()) {
            throw new RuntimeException("Game not found with id: " + gameId);
        }
        Game game = optionalGame.get();

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

    public String generateRandomMove(String mode) {
        List<String> validMoves = mode.equalsIgnoreCase("CLASSIC") ? CLASSIC_MOVES : SPOCK_MOVES;
        return validMoves.get(random.nextInt(validMoves.size()));
    }

    private boolean classicRules(String player1Move, String player2Move) {
        return (player1Move.equals("ROCK") && player2Move.equals("SCISSORS")) ||
                (player1Move.equals("PAPER") && player2Move.equals("ROCK")) ||
                (player1Move.equals("SCISSORS") && player2Move.equals("PAPER"));
    }

    private boolean additionalSpockRules(String player1Move, String player2Move) {
        return (player1Move.equals("ROCK") && player2Move.equals("LIZARD")) ||
                (player1Move.equals("LIZARD") && player2Move.equals("SPOCK")) ||
                (player1Move.equals("SPOCK") && player2Move.equals("SCISSORS")) ||
                (player1Move.equals("SCISSORS") && player2Move.equals("LIZARD")) ||
                (player1Move.equals("LIZARD") && player2Move.equals("PAPER")) ||
                (player1Move.equals("PAPER") && player2Move.equals("SPOCK")) ||
                (player1Move.equals("SPOCK") && player2Move.equals("ROCK"));
    }

    public Map<String, Long> getRoundsWonForOngoingGame(Long gameId) {
        Optional<Game> optionalGame = Optional.ofNullable(gameService.findById(gameId));
        if (optionalGame.isEmpty()) {
            throw new RuntimeException("Game not found with id: " + gameId);
        }
        Game game = optionalGame.get();

        if (!game.getResult().equals(Game.GameResult.ONGOING)) {
            throw new RuntimeException("Game is not ongoing.");
        }

        List<Round> rounds = roundService.findByGameId(gameId);

        Map<Round.RoundResult, Long> resultCounts = rounds.stream()
                .map(Round::getResult)
                .filter(result -> result == Round.RoundResult.PLAYER1_WIN || result == Round.RoundResult.PLAYER2_WIN)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        long player1Wins = resultCounts.getOrDefault(Round.RoundResult.PLAYER1_WIN, 0L);
        long player2Wins = resultCounts.getOrDefault(Round.RoundResult.PLAYER2_WIN, 0L);

        return Map.of("player1Wins", player1Wins, "player2Wins", player2Wins);
    }
}
