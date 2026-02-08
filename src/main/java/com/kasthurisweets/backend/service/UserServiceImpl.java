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

    // ✅ REQUIRED FIELDS
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    // ✅ CONSTRUCTOR
    public UserServiceImpl(UserRepository userRepository,
                           JwtUtil jwtUtil,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * INTERNAL USER CREATION
     * Used by admin / system / tests
     */
    @Override
    public User createUser(User user) {

        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        return userRepository.save(user);
    }

    /**
     * PUBLIC REGISTER API
     */
    @Override
    public void register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        // 🔐 PASSWORD ENCRYPTION
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setRole("USER");

        // 🔐 EMAIL VERIFICATION
        user.setEmailVerified(false);
        user.setVerificationToken(UUID.randomUUID().toString());

        userRepository.save(user);

        // ⚠️ TEMP ONLY (replace with email service later)
        System.out.println(
                "VERIFY EMAIL → http://localhost:8080/api/auth/verify?token="
                        + user.getVerificationToken()
        );
    }

    /**
     * LOGIN
     */
    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password")
                );

        // 🔐 PASSWORD MATCH
        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            throw new RuntimeException("Invalid email or password");
        }

        // ❌ BLOCK LOGIN IF EMAIL NOT VERIFIED
        if (!user.isEmailVerified()) {
            throw new RuntimeException("Please verify your email before login");
        }

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole()
        );

        return new LoginResponse(token);
    }


    /**
     * GET USER BY ID
     */
    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found with id " + id)
                );
    }

    @Override
    public void verifyEmail(String token) {

        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        user.setEmailVerified(true);
        user.setVerificationToken(null); // 🔐 one-time use

        userRepository.save(user);
    }


    /**
     * GET ALL USERS
     */
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
