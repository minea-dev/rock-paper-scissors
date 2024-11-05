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
    @ManyToOne
    @JoinColumn(name = "mode_id", nullable = false)
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
    @Column(nullable = true)
    private GameResult result;

    @Getter
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Game() {}

    public enum GameResult {
        PLAYER1_WIN, PLAYER2_WIN, DRAW
    }
}


