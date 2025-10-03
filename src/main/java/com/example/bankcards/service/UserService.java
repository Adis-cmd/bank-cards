package com.example.bankcards.service;

import com.example.bankcards.dto.AuthDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    User findUserByEmail(String email);

    Page<UserDto> getAllUser(Pageable pageable);

    void register(AuthDto dto);

    void deleteUser(String email);
}
