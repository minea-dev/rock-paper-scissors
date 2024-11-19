package org.rockpaperscissors.backend.controller;

import org.rockpaperscissors.backend.model.Game;
import org.rockpaperscissors.backend.model.Round;
import org.rockpaperscissors.backend.model.User;
import org.rockpaperscissors.backend.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/game")
public class GameController {

    @Autowired
    private GameService gameService;

    @Autowired
    private GameLogicService gameLogicService;

    @Autowired
    private RoundService roundService;

    @Autowired
    private UserService userService;

    // 1- GAME --> CONFIGURED
    @PostMapping("/start")
    public ResponseEntity<?> startGame(@RequestBody Map<String, Object> gameData) {
        try {
            Long player1Id = Long.valueOf(gameData.get("player1Id").toString());
            String modeString = gameData.get("mode").toString(); // The mode string coming from the frontend
            int rounds = Integer.parseInt(gameData.get("rounds").toString());
            boolean isRealTime = Boolean.parseBoolean(gameData.get("isRealTime").toString());

            Optional<User> player1 = userService.findById(player1Id);
            if (player1.isEmpty()) {
                throw new IllegalArgumentException("Invalid player ID.");
            }

            if (Game.GameMode.fromString(modeString) == null) {
                throw new IllegalArgumentException("Invalid game mode.");
            }
            Game.GameMode mode = Game.GameMode.fromString(modeString);

            Game newGame = new Game();
            newGame.setPlayer1(player1.get());
            newGame.setPlayer2(null);
            newGame.setMode(mode);
            newGame.setRounds(rounds);
            newGame.setIsRealTime(isRealTime);
            newGame.setResult(Game.GameResult.CONFIGURED);
            if (isRealTime) {
                newGame.setGameState(Game.GameState.WAITING);
            }
            gameService.save(newGame);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "gameId", newGame.getId()
            ));
        } catch (Exception e) {
            throw new RuntimeException("Error parsing game data: " + e.getMessage(), e);
        }
    }

    // 1.2- GAME --> CONFIGURED FOR USER2
    @PostMapping("/set-player2")
    public ResponseEntity<?> setGameForPlayer2(@RequestBody Map<String, Object> gameData) {
        try {
            Long gameId = Long.valueOf(gameData.get("gameId").toString());
            Long player2Id = Long.valueOf(gameData.get("player2Id").toString());

            Game confiGame = gameService.findById(gameId);
            Optional<User> player2 = userService.findById(player2Id);

            confiGame.setPlayer2(player2.get());
            confiGame.setGameState(Game.GameState.IN_PROGRESS);

            gameService.save(confiGame);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "gameId", confiGame.getId()
            ));
        } catch (Exception e) {
            throw new RuntimeException("Error parsing game data: " + e.getMessage(), e);
        }
    }

    // 2- GAME --> Sacar game por Id
    @GetMapping("/game-details")
    public ResponseEntity<?> gameDetails(@RequestHeader("gameId") String gameId) {
        Long id = Long.valueOf(gameId);

        Game game = gameService.findById(id);
        User player1 = game.getPlayer1();
        User player2 = game.getPlayer2();

        if (game == null) {
            return ResponseEntity.status(404).body(Map.of(
                    "success", false,
                    "message", "Game not found"
            ));
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "realTime", game.getIsRealTime(),
                "result", game.getResult().name(),
                "rounds", game.getRounds(),
                "mode", game.getMode().name(),
                "player1ID", player1.getId().toString(),
                "player2ID", player2 != null ? player2.getId().toString() : "",
                "player1Name", player1.getUsername(),
                "player2Name", player2 != null ? player2.getUsername() : "",
                "remainingRounds", game.getRounds() - roundService.findByGameId(id).size()
        ));
    }

    // 3 - JUGAR RONDA CONTRA MÁQUINA
    @PostMapping("/play-round-machine")
    public ResponseEntity<?> playRoundMachine(@RequestBody Map<String, Object> gameData) {
        try {
            Long gameId = Long.valueOf(gameData.get("gameId").toString());
            Long player1Id = Long.valueOf(gameData.get("player1Id").toString());
            int rounds = Integer.parseInt(gameData.get("rounds").toString());
            String player1Move = gameData.get("player1Move").toString();

            Game game = gameService.findById(gameId);
            if (game == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Game not found"));
            }

            if (!game.getResult().equals(Game.GameResult.ONGOING) && !game.getResult().equals(Game.GameResult.CONFIGURED)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Game has already concluded"));
            }

            if (!game.getPlayer1().getId().equals(player1Id)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Player1 is not part of this game"));
            }

            if (game.getResult().equals(Game.GameResult.CONFIGURED) && game.getRounds() > 1) {
                game.setResult(Game.GameResult.ONGOING);
                gameService.save(game);
            }
            String modeString = String.valueOf(game.getMode());
            String player2Move = gameLogicService.generateRandomMove(modeString);
            Game.GameResult roundResult = gameLogicService.determineRoundResult(player1Move, player2Move, modeString);

            Round round = new Round();
            round.setGame(game);
            round.setRoundNumber(roundService.findByGameId(gameId).size() + 1);
            round.setPlayer1Move(Round.MoveName.valueOf(player1Move));
            round.setPlayer2Move(Round.MoveName.valueOf(player2Move));
            round.setResult(Round.RoundResult.fromGameResult(roundResult));
            roundService.save(round);

            if (game.getResult().equals(Game.GameResult.ONGOING) || game.getResult().equals(Game.GameResult.CONFIGURED) && game.getRounds() == 1) {
                List<Round> roundsPlayed = roundService.findByGameId(gameId);
                if (roundsPlayed.size() == rounds) {
                    gameLogicService.determineGameResult(gameId);
                }
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "roundNumber", round.getRoundNumber(),
                    "player1Move", round.getPlayer1Move(),
                    "player2Move", round.getPlayer2Move(),
                    "roundResult", roundResult.toString(),
                    "remainingRounds", rounds - round.getRoundNumber(),
                    "gameResult", game.getResult().toString()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // JUGAR RONDA CONTRA OPONENTE
    // 3 - JUGAR RONDA CONTRA MÁQUINA
    @PostMapping("/play-round-human")
    public ResponseEntity<?> playRoundHuman(@RequestBody Map<String, Object> gameData) {
        try {
            Long gameId = Long.valueOf(gameData.get("gameId").toString());
            int playingNow = Integer.parseInt(gameData.get("numPlayer").toString());
            int currentRound = Integer.parseInt(gameData.get("currentRound").toString());
            String move = gameData.get("move").toString();

            Game game = gameService.findById(gameId);

            if (game == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Game not found"));
            }

            if (!game.getResult().equals(Game.GameResult.ONGOING) && !game.getResult().equals(Game.GameResult.CONFIGURED)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Game has already concluded"));
            }

            if (game.getResult().equals(Game.GameResult.CONFIGURED) && game.getRounds() > 1) {
                game.setResult(Game.GameResult.ONGOING);
                gameService.save(game);
            }

            String modeString = String.valueOf(game.getMode());

            if (roundService.findByGameId(gameId).size() == currentRound + 1 ) {
                Round playingRound = roundService.findLastRound(gameId);
                if (playingNow == 1) {
                    playingRound.setPlayer1Move(Round.MoveName.valueOf(move));
                } else if (playingNow == 2) {
                    playingRound.setPlayer2Move(Round.MoveName.valueOf(move));
                }
                if (playingRound.getPlayer1Move().name().equals("NONE") || playingRound.getPlayer2Move().name().equals("NONE") ) {
                    roundService.save(playingRound);
                    return ResponseEntity.ok(Map.of(
                            "success", true,
                            "playedBy2", false,
                            "roundId", playingRound.getId(),
                            "player2Name", game.getPlayer2() != null ? game.getPlayer2().getUsername() : ""
                    ));
                }
                Game.GameResult roundResult = gameLogicService.determineRoundResult(playingRound.getPlayer1Move().name(), playingRound.getPlayer2Move().name(), modeString);
                playingRound.setResult(Round.RoundResult.fromGameResult(roundResult));
                roundService.save(playingRound);

                if (game.getResult().equals(Game.GameResult.ONGOING) || game.getResult().equals(Game.GameResult.CONFIGURED) && game.getRounds() == 1) {
                    List<Round> roundsPlayed = roundService.findByGameId(gameId);
                    if (roundsPlayed.size() == game.getRounds()) {
                        game.setGameState(Game.GameState.FINISHED);
                        gameService.save(game);
                        gameLogicService.determineGameResult(gameId);
                    }
                }

                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "playedBy2", true,
                        "roundId", playingRound.getId(),
                        "roundNumber", playingRound.getRoundNumber(),
                        "player1Move", playingRound.getPlayer1Move(),
                        "player2Move", playingRound.getPlayer2Move(),
                        "roundResult", roundResult.toString(),
                        "remainingRounds", game.getRounds() - playingRound.getRoundNumber(),
                        "gameResult", game.getResult().toString(),
                        "player2Name", game.getPlayer2() != null ? game.getPlayer2().getUsername() : ""
                ));
            }

            Round round = new Round();
            round.setGame(game);
            round.setRoundNumber(roundService.findByGameId(gameId).size() + 1);
            if (playingNow == 1) {
                round.setPlayer1Move(Round.MoveName.valueOf(move));
                round.setPlayer2Move(Round.MoveName.NONE);
            } else if (playingNow == 2) {
                round.setPlayer2Move(Round.MoveName.valueOf(move));
                round.setPlayer1Move(Round.MoveName.NONE);
            }
            round.setResult(Round.RoundResult.CONFIGURED);
            roundService.save(round);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "playedBy2", false,
                    "roundId", round.getId(),
                    "player2Name", game.getPlayer2() != null ? game.getPlayer2().getUsername() : ""
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // Chequear para sacar resultado
    @GetMapping("/round-details")
    public ResponseEntity<?> roundDetails(@RequestHeader("roundId") String roundId) {
        Long id = Long.valueOf(roundId);
        Round currentRound = roundService.findById(id).orElse(null);

        assert currentRound != null;
        if (currentRound.getResult() == Round.RoundResult.CONFIGURED) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "playedBy2", false,
                    "roundId", currentRound.getId()
            ));
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "playedBy2", true,
                "roundNumber", currentRound.getRoundNumber(),
                "player1Move", currentRound.getPlayer1Move(),
                "player2Move", currentRound.getPlayer2Move(),
                "roundResult", currentRound.getResult().name(),
                "remainingRounds", currentRound.getGame().getRounds() - currentRound.getRoundNumber(),
                "gameResult", currentRound.getGame().getResult().toString()
        ));
    }
}