package com.example.bankcards.service.impl;

import com.example.bankcards.dto.AuthDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.AuthorityService;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final AuthorityService authorityService;
    private final PasswordEncoder passwordEncoder;


    @Override
    public User findUserByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow();
    }


    @Override
    public String register(AuthDto dto) {
        User newUser = User.builder()
                .name(dto.getName())
                .surname(dto.getSurname())
                .authority(authorityService.findAuthorityByName("USER"))
                .enabled(true)
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .build();

        repository.save(newUser);
        String tokenJWT = "";
        return tokenJWT;
    }

}
