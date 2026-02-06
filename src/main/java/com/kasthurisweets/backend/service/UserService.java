package com.kasthurisweets.backend.service;

import com.kasthurisweets.backend.dto.LoginRequest;
import com.kasthurisweets.backend.dto.LoginResponse;
import com.kasthurisweets.backend.dto.RegisterRequest;
import com.kasthurisweets.backend.entity.User;

import java.util.List;

public interface UserService {

    User createUser(User user);

    User getUserById(Long id);

    List<User> getAllUsers();

    LoginResponse login(LoginRequest request);

    void register(RegisterRequest request);

    void verifyEmail(String token);



}
