package com.example.bankcards.service;

import com.example.bankcards.dto.AuthDto;
import com.example.bankcards.entity.User;

public interface UserService {
    User findUserByEmail(String email);

    String register(AuthDto dto);
}
