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
    @ManyToOne
    @JoinColumn(name = "player1_move_id", nullable = false)
    private Move player1Move;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "player2_move_id")
    private Move player2Move;

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
        PLAYER1_WIN, PLAYER2_WIN, DRAW
    }
}

