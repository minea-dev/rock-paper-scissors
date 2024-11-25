package org.rockpaperscissors.backend.controller;

import org.rockpaperscissors.backend.model.User;
import org.rockpaperscissors.backend.service.PasswordService;
import org.rockpaperscissors.backend.service.UserService;
import org.rockpaperscissors.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PasswordService passwordService;

    /**
     * Registers a new user and returns a token for authentication.
     *
     * @param newUser User details sent in the request body.
     * @return A ResponseEntity with registration status and user details or an error message.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User newUser) {
        try {
            if (userService.findByEmail(newUser.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().body("Error: The email is already in use.");
            }

            if (newUser.getPassword().isEmpty() || newUser.getEmail().isEmpty() || newUser.getUsername().isEmpty()) {
                return ResponseEntity.badRequest().body("Error: Missing data.");
            }
            String encryptedPassword = passwordService.encryptPassword(newUser.getPassword());
            newUser.setPassword(encryptedPassword);
            newUser.setRegistered(true);
            User savedUser = userService.save(newUser);

            String token = jwtUtil.generateToken(savedUser.getUsername());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "User registered successfully.",
                    "id", savedUser.getId(),
                    "name", savedUser.getUsername(),
                    "token", token
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error.");
        }
    }

    /**
     * Creates a guest user without email or password and returns a token.
     *
     * @param request A map containing the username of the guest player.
     * @return A ResponseEntity with guest user details or an error message.
     */
    @PostMapping("/guest-player")
    public ResponseEntity<?> loginAsGuest(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        if (username == null || username.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Username is required.");
        }
        User guestUser = new User();
        guestUser.setUsername(username);
        guestUser.setPassword(null);
        guestUser.setEmail(null);
        guestUser.setRegistered(false);

        User savedGuest = userService.save(guestUser);

        // Generar el token
        String token = jwtUtil.generateToken(savedGuest.getUsername());

        // Responder con los datos necesarios
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "User registered successfully.",
                "id", savedGuest.getId(),
                "name", savedGuest.getUsername(),
                "token", token
        ));
    }

    /**
     * Authenticates a user using email and password, returning a token on success.
     *
     * @param credentials A map containing the email and password.
     * @return A ResponseEntity with login status, user details, and token or an error message.
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Email not registered"));
        }
        User user = userOpt.get();
        if (!passwordService.checkPassword(password, user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Incorrect password"));
        }
        String token = jwtUtil.generateToken(user.getUsername());
        return ResponseEntity.ok(Map.of(
                "success", true,
                "id", user.getId(),
                "name", user.getUsername(),
                "token", token
        ));
    }

    /**
     * Checks the validity of a session token.
     *
     * @param token The authorization token from the request header.
     * @return A ResponseEntity indicating whether the session is valid.
     */
    @GetMapping("/check-session")
    public ResponseEntity<?> checkSession(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (jwtUtil.isTokenValid(token)) {
            return ResponseEntity.ok().body("Valid session");
        } else {
            System.out.println("Invalid token");
            return ResponseEntity.status(401).body("Invalid or expired token");
        }
    }
}