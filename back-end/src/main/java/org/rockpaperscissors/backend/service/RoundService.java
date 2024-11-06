package org.rockpaperscissors.backend.service;

import org.rockpaperscissors.backend.model.Move;
import org.rockpaperscissors.backend.model.Round;
import org.rockpaperscissors.backend.repository.GameRepository;
import org.rockpaperscissors.backend.repository.RoundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RoundService {

    @Autowired
    private RoundRepository roundRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private MoveService moveService;

    public List<Round> findAll() {
        return roundRepository.findAll();
    }

    public Optional<Round> findById(Long id) {
        return roundRepository.findById(id);
    }

    public List<Round> findByGameId(Long gameId) {
        return roundRepository.findByGameId(gameId);
    }

    public Optional<Round> findByGameIdAndRoundNumber(Long gameId, int roundNumber) {
        return roundRepository.findByGameIdAndRoundNumber(gameId, roundNumber).stream().findFirst();
    }

    public Round save(Round round) {
        return roundRepository.save(round);
    }

    public Round playRound(long gameId, String player1Move, String player2Move, String roundResult) {
        Round newRound = new Round();
        newRound.setGame(gameRepository.findById(gameId));
        newRound.setRoundNumber(gameRepository.findById(gameId).getRounds());
        newRound.setPlayer1Move(moveService.convertStringToMove(player1Move));
        newRound.setPlayer2Move(moveService.convertStringToMove(player2Move));
        newRound.setResult(Round.RoundResult.valueOf(roundResult.toUpperCase()));

        return roundRepository.save(newRound);
    }
}

