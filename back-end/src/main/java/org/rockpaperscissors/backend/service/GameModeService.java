package org.rockpaperscissors.backend.service;

import org.rockpaperscissors.backend.model.GameMode;
import org.rockpaperscissors.backend.repository.GameModeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class GameModeService {

    @Autowired
    private GameModeRepository gameModeRepository;

    public List<GameMode> findAll() {
        return gameModeRepository.findAll();
    }

    public Optional<GameMode> findById(Long id) {
        return gameModeRepository.findById(id);
    }

    public Optional<GameMode> findByModeName(String modeName) {
        return Optional.ofNullable(gameModeRepository.findByModeName(modeName));
    }
}

