package com.example.demo.auth;

import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestParam(required = false) String email,
                                    @RequestParam(required = false) String password,
                                    @RequestBody(required = false) Map<String, String> body) {
        if (email == null && body != null) email = body.get("email");
        if (password == null && body != null) password = body.get("password");
        if (email == null || password == null) return ResponseEntity.badRequest().body("email and password required");
        if (userRepository.existsByEmail(email)) return ResponseEntity.badRequest().body("Email already registered");

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setCreatedAt(Instant.now());
        userRepository.save(user);
        return ResponseEntity.ok("Signup successful");
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestParam(required = false) String email,
                                    @RequestParam(required = false) String password,
                                    @RequestBody(required = false) Map<String, String> body) {
        if (email == null && body != null) email = body.get("email");
        if (password == null && body != null) password = body.get("password");
        if (email == null || password == null) return ResponseEntity.badRequest().body("email and password required");

        Optional<User> opt = userRepository.findByEmail(email);
        if (opt.isEmpty()) return ResponseEntity.status(401).body("Invalid credentials");
        User user = opt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(Map.of("token", token));
    }
}
