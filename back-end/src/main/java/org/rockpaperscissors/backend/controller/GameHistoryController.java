package org.rockpaperscissors.backend.controller;

import org.rockpaperscissors.backend.model.Game;
import org.rockpaperscissors.backend.model.Round;
import org.rockpaperscissors.backend.service.GameService;
import org.rockpaperscissors.backend.service.RoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/history")
public class GameHistoryController {

    @Autowired
    private GameService gameService;
    @Autowired
    private RoundService roundsService;

    /**
     * Endpoint to get game details by gameId.
     *
     * @param gameId the ID of the game sent in the request header.
     * @return ResponseEntity containing game details.
     */
    @GetMapping("/game-details")
    public ResponseEntity<?> gameDetails(@RequestHeader("gameId") String gameId) {
        try {
            Game game = gameService.findById(Long.parseLong(gameId));
            List<Round> rounds = roundsService.findByGameId(Long.parseLong(gameId));
            String player2;
            if (game.getPlayer2() == null) {
                player2 = "Machine";
            } else {
                player2 = game.getPlayer2().getUsername();
            }
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "gameId", game.getId(),
                    "player1", game.getPlayer1().getUsername(),
                    "player2", player2,
                    "rounds", rounds.size(),
                    "gameResult", game.getResult().toString(),
                    "roundDetails", rounds.stream().map(round -> Map.of(
                            "roundNumber", round.getRoundNumber(),
                            "player1Move", round.getPlayer1Move(),
                            "player2Move", round.getPlayer2Move(),
                            "roundResult", round.getResult().toString()
                    )).toList()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error retrieving game details: " + e.getMessage()
            ));
        }
    }


    @GetMapping("/user-games")
    public ResponseEntity<?> userGameDetails(@RequestHeader("userId") String userId) {
        try {
            Long parsedUserId = Long.parseLong(userId);

            List<Game> games = gameService.findByPlayerId(parsedUserId);

            List<Map<String, Object>> gameDetails = games.stream().map(game -> {
                List<Round> rounds = roundsService.findByGameId(game.getId());
                String player2;
                if (game.getPlayer2() == null) {
                    player2 = "Machine";
                } else {
                    player2 = game.getPlayer2().getUsername();
                }
                return Map.of(
                        "gameId", game.getId(),
                        "time", game.getCreatedAt().toString(),
                        "player1", game.getPlayer1().getUsername(),
                        "player2", player2,
                        "rounds", rounds.size(),
                        "mode", game.getMode().toString(),
                        "gameResult", game.getResult().toString(),
                        "roundDetails", rounds.stream().map(round -> Map.of(
                                "roundNumber", round.getRoundNumber(),
                                "player1Move", round.getPlayer1Move(),
                                "player2Move", round.getPlayer2Move(),
                                "roundResult", round.getResult().toString()
                        )).toList()
                );
            }).toList();

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "userId", userId,
                    "totalGames", games.size(),
                    "games", gameDetails
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error retrieving user game details: " + e.getMessage()
            ));
        }
    }


}
