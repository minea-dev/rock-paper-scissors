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

    @GetMapping("/test")
    public String testEndpoint() {
        return "Test successful!";
    }

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

    @PostMapping("/guest-player")
    public ResponseEntity<?> loginAsGuest(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        System.out.println(username);
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

    @GetMapping("/email")
    public ResponseEntity<?> getUserByEmail(@RequestParam String email) {
        return userService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}