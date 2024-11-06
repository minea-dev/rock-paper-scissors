package org.rockpaperscissors.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
public class User {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @Column(unique = true, nullable = false)
    private String username;

    @Getter
    @Setter
    @Column(unique = true)
    private String email;

    @Getter
    @Setter
    private String password;

    @Getter
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Getter
    @Setter
    private boolean registered;

    public User() {}

    public User(Long id, String username, String email, String password, boolean registered) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.registered = registered;
    }
}

