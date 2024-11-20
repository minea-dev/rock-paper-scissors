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
     * Endpoint to verify if the server is running correctly.
     *
     * @return A string message indicating the test was successful.
     */
    @GetMapping("/test")
    public String testEndpoint() {
        return "Test successful!";
    }

    /**
     * Endpoint for user registration. It checks if the email is already taken,
     * encrypts the password, and generates a JWT token for the user.
     *
     * @param newUser The new user object containing registration details.
     * @return A response containing a success message, user details, and a JWT token.
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
     * Endpoint for logging in as a guest player. A guest player is registered without an email or password,
     * and only a username is required. A JWT token is generated for the guest player.
     *
     * @param request A map containing the username of the guest player.
     * @return A response containing a success message, guest player details, and a JWT token.
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

        String token = jwtUtil.generateToken(savedGuest.getUsername());

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "User registered successfully.",
                "id", savedGuest.getId(),
                "name", savedGuest.getUsername(),
                "token", token
        ));
    }

    /**
     * Endpoint for user login. This method checks if the provided email exists in the database and if the password
     * matches the stored password. If successful, a JWT token is generated for the user.
     *
     * @param credentials A map containing the user's email and password.
     * @return A response containing a success message, user details, and a JWT token.
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
     * Endpoint to check if a session is valid by validating the JWT token.
     *
     * @param token The JWT token passed in the Authorization header.
     * @return A response indicating whether the session is valid or if the token has expired.
     */
    @GetMapping("/check-session")
    public ResponseEntity<?> checkSession(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (jwtUtil.isTokenValid(token)) {
            return ResponseEntity.ok().body("Valid session");
        } else {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }
    }

    /**
     * Endpoint to retrieve a user by their email. This method searches for the user in the database by their email.
     *
     * @param email The email of the user to retrieve.
     * @return A response containing the user details if found, or a 404 not found error if the email is not registered.
     */
    @GetMapping("/email")
    public ResponseEntity<?> getUserByEmail(@RequestParam String email) {
        return userService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}