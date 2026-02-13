package com.kasthurisweets.backend.service;

import com.kasthurisweets.backend.dto.LoginRequest;
import com.kasthurisweets.backend.dto.LoginResponse;
import com.kasthurisweets.backend.dto.RegisterRequest;
import com.kasthurisweets.backend.entity.User;
import com.kasthurisweets.backend.repository.UserRepository;
import com.kasthurisweets.backend.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UserServiceImpl(
            UserRepository userRepository,
            JwtUtil jwtUtil,
            PasswordEncoder passwordEncoder,
            EmailService emailService
    ) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    // ================= REGISTER =================
    @Override
    public void register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        user.setEmailVerified(false);

        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);

        userRepository.save(user);

        String verifyLink = "http://localhost:8080/api/auth/verify?token=" + token;

        emailService.sendEmail(
                user.getEmail(),
                "Verify your email - Kasthuri Sweets",
                "Hi " + user.getName() + ",\n\n" +
                        "Please verify your email by clicking the link below:\n" +
                        verifyLink + "\n\n" +
                        "Thank you,\nKasthuri Sweets"
        );

        System.out.println("VERIFY EMAIL LINK SENT: " + verifyLink);
    }

    // ================= LOGIN =================
    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password")
                );

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            throw new RuntimeException("Invalid email or password");
        }

        if (!user.isEmailVerified()) {
            throw new RuntimeException("Please verify your email before login");
        }

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole()
        );

        // 🔥 IMPORTANT: Return role + email also
        return new LoginResponse(
                token,
                user.getEmail(),
                user.getRole()
        );
    }

    // ================= VERIFY EMAIL =================
    @Override
    public void verifyEmail(String token) {

        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() ->
                        new RuntimeException("Invalid verification token")
                );

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);
    }

    // ================= EXTRA =================
    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
