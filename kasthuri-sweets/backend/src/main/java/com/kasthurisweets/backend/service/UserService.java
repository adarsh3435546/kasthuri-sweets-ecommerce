package com.kasthurisweets.backend.service;

import com.kasthurisweets.backend.dto.LoginRequest;
import com.kasthurisweets.backend.dto.LoginResponse;
import com.kasthurisweets.backend.dto.RegisterRequest;
import com.kasthurisweets.backend.entity.User;

import java.util.List;

public interface UserService {

    void register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    void verifyEmail(String token);

    User getUserById(Long id);

    List<User> getAllUsers();
}
