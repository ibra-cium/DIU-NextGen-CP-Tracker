package com.nextgen.DiuNextGen.controller;

import com.nextgen.DiuNextGen.dto.RegisterRequest;
import com.nextgen.DiuNextGen.entity.User;
import com.nextgen.DiuNextGen.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.nextgen.DiuNextGen.dto.LoginRequest;
import com.nextgen.DiuNextGen.dto.LoginResponse;
import com.nextgen.DiuNextGen.config.JwtUtil;
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // POST http://localhost:8081/api/auth/register
    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        // Check if user exists
        if (userRepository.findByVjudgeHandle(request.getVjudgeHandle()) != null) {
            throw new RuntimeException("User already exists!");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setBatch(request.getBatch());
        user.setVjudgeHandle(request.getVjudgeHandle());
        user.setRole("USER"); // Default role

        // CRITICAL: Hash the password before saving
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);
        return "User registered successfully!";
    }
    @Autowired
    private JwtUtil jwtUtil; // Inject the badge maker

    // POST http://localhost:8081/api/auth/login
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        User user = userRepository.findByVjudgeHandle(request.getVjudgeHandle());
        if (user == null) throw new RuntimeException("User not found!");

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password!");
        }

        String token = jwtUtil.generateToken(user.getVjudgeHandle());

        // --- UPDATE THIS LINE ---
        return new LoginResponse(token, user.getRole());
    }
}