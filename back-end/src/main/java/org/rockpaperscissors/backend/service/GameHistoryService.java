package org.rockpaperscissors.backend.service;

import org.rockpaperscissors.backend.model.GameHistory;
import org.rockpaperscissors.backend.repository.GameHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class GameHistoryService {

    @Autowired
    private GameHistoryRepository gameHistoryRepository;

    public List<GameHistory> findAll() {
        return gameHistoryRepository.findAll();
    }

    public Optional<GameHistory> findById(Long id) {
        return gameHistoryRepository.findById(id);
    }

    public List<GameHistory> findByUserId(Long userId) {
        return gameHistoryRepository.findByUserId(userId);
    }

    public List<GameHistory> findByGameId(Long gameId) {
        return gameHistoryRepository.findByGameId(gameId);
    }

    public List<GameHistory> findByUserIdAndResult(String result) {
        return gameHistoryRepository.findByUserIdAndResult(result);
    }

    public GameHistory save(GameHistory gameHistory) {
        return gameHistoryRepository.save(gameHistory);
    }
}

