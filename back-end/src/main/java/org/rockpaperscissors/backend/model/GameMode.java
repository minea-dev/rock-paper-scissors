package org.rockpaperscissors.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
public class GameMode {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModeName modeName;

    public GameMode() {}

    public enum ModeName {
        CLASSIC, SPOCK
    }
}

