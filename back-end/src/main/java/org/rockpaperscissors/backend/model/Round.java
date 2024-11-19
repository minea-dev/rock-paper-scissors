package org.rockpaperscissors.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
public class Round {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Getter
    @Setter
    @Column(name = "round_number", nullable = false)
    private Integer roundNumber;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "player1_move")
    private MoveName player1Move;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "player2_move")
    private MoveName player2Move;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoundResult result;

    @Getter
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Round() {}

    public enum RoundResult {
        PLAYER1_WIN, PLAYER2_WIN, DRAW, CONFIGURED, ONGOING;

        public static RoundResult fromGameResult(Game.GameResult gameResult) {
            switch (gameResult) {
                case PLAYER1_WIN: return PLAYER1_WIN;
                case PLAYER2_WIN: return PLAYER2_WIN;
                case DRAW: return DRAW;
                case CONFIGURED: return CONFIGURED;
                case ONGOING:
                    throw new IllegalArgumentException("Cannot determine round result from game state: " + gameResult);
                default:
                    throw new IllegalArgumentException("Invalid GameResult: " + gameResult);
            }
        }
    }

    public enum MoveName {
        ROCK, PAPER, SCISSORS, LIZARD, SPOCK, NONE;

        public static MoveName fromString(String move) {
            for (MoveName moveName : MoveName.values()) {
                if (moveName.name().equalsIgnoreCase(move)) {
                    return moveName;
                }
            }
            throw new IllegalArgumentException("Invalid move: " + move);
        }
    }
}