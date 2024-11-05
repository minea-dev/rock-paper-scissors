package org.rockpaperscissors.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
public class GameHistory {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameResult result;

    @Getter
    @Column(name = "played_at", nullable = false, updatable = false)
    private LocalDateTime playedAt = LocalDateTime.now();

    public GameHistory() {}

    public enum GameResult {
        WIN, LOSS, DRAW
    }
}

