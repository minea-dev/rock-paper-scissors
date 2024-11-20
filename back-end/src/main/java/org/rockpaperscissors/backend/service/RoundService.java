package org.rockpaperscissors.backend.service;

import org.rockpaperscissors.backend.model.Round;
import org.rockpaperscissors.backend.model.Round.MoveName;
import org.rockpaperscissors.backend.repository.GameRepository;
import org.rockpaperscissors.backend.repository.RoundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class RoundService {

    @Autowired
    private RoundRepository roundRepository;

    @Autowired
    private GameRepository gameRepository;

    public List<Round> findAll() {
        return roundRepository.findAll();
    }

    public Optional<Round> findById(Long id) {
        return roundRepository.findById(id);
    }

    public List<Round> findByGameId(Long gameId) {
        return roundRepository.findByGameId(gameId);
    }
    public Round findLastRound(Long gameId) {
        List<Round> rounds = roundRepository.findByGameId(gameId);
        if (rounds != null && !rounds.isEmpty()) {
            return rounds.stream()
                    .max(Comparator.comparing(Round::getRoundNumber))
                    .orElse(null);
        }
        return null;
    }

    public Round save(Round round) {
        return roundRepository.save(round);
    }
}
