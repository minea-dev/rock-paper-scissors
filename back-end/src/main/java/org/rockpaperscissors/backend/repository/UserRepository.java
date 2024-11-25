package org.rockpaperscissors.backend.repository;

import org.rockpaperscissors.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    List<User> findByUsername(String username);
}
