package org.rockpaperscissors.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
public class Game {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameMode mode;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "player1_id", nullable = false)
    private User player1;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "player2_id")
    private User player2;

    @Getter
    @Setter
    @Column(nullable = false)
    private Integer rounds;

    @Getter
    @Setter
    @Column(name = "is_real_time", nullable = false)
    private Boolean isRealTime = false;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameResult result;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "game_state")
    private GameState gameState;  // Nullable GameState based on isRealTime

    @Getter
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Game() {}

    public enum GameMode {
        CLASSIC, SPOCK;

        public static GameMode fromString(String mode) {
            for (GameMode gameMode : GameMode.values()) {
                if (gameMode.name().equalsIgnoreCase(mode)) {
                    return gameMode;
                }
            }
            return null;
        }
    }

    public enum GameResult {
        PLAYER1_WIN, PLAYER2_WIN, DRAW, CONFIGURED, ONGOING;

        public static GameResult fromString(String result) {
            for (GameResult gameResult : GameResult.values()) {
                if (gameResult.name().equalsIgnoreCase(result)) {
                    return gameResult;
                }
            }
            return null;
        }
    }

    public enum GameState {
        WAITING, IN_PROGRESS, ABANDONED, FINISHED;

        public static GameState fromString(String state) {
            for (GameState gameState : GameState.values()) {
                if (gameState.name().equalsIgnoreCase(state)) {
                    return gameState;
                }
            }
            return null;
        }
    }
}