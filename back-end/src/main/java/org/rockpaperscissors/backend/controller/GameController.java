package org.rockpaperscissors.backend.controller;

import org.rockpaperscissors.backend.model.Game;
import org.rockpaperscissors.backend.model.Round;
import org.rockpaperscissors.backend.service.GameService;
import org.rockpaperscissors.backend.service.RoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/game")
public class GameController {

    @Autowired
    private GameService gameService;

    @Autowired
    private GameLogicService gameLogicService;

    @Autowired
    private RoundService roundService;

    // Start a new game
    @PostMapping("/start")
    public Game startGame(@RequestParam Long player1Id,
                          @RequestParam(required = false) Long player2Id,
                          @RequestParam String mode,
                          @RequestParam int rounds,
                          @RequestParam boolean isRealTime) {
        return gameService.startNewGame(player1Id, player2Id, mode, rounds, isRealTime);
    }

    // Play a round (single-player or multiplayer without WebSocket)
    @PostMapping("/{gameId}/play")
    public Round playRound(@PathVariable Long gameId,
                           @RequestParam String player1Move,
                           @RequestParam(required = false) String player2Move) {
        Game game = gameService.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid game ID"));
    // OJO CON ESTO!!
        if (!game.getIsRealTime()) {
            player2Move = gameLogicService.generateRandomMove(game.getMode().getModeName().toString());
        }

        String roundResult = gameLogicService.determineRoundResult(player1Move, player2Move, game.getMode().getModeName().toString());

        return roundService.playRound(gameId, player1Move, player2Move, roundResult);
    }

    // Get game result after rounds are complete
    @GetMapping("/{gameId}/result")
    public String getGameResult(@PathVariable Long gameId) {
        return gameLogicService.determineGameResult(gameId);
    }

    // Get a list of rounds in the game
    @GetMapping("/{gameId}/rounds")
    public List<Round> getRounds(@PathVariable Long gameId) {
        return roundService.findByGameId(gameId);
    }
}

