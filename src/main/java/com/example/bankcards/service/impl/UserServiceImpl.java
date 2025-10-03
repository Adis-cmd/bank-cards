package com.example.bankcards.service.impl;

import com.example.bankcards.dto.AuthDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.EmailAlreadyExistsException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.AuthorityService;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<UserDto> getAllUser(Pageable pageable) {
        Page<User> users = repository.findAll(pageable);

        return users.map(user -> new UserDto(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getEmail()
        ));
    }


    @Override
    public void register(AuthDto dto) {

        if (repository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException("Пользователь с email " + dto.getEmail() + " уже существует");
        }

        User newUser = User.builder()
                .name(dto.getName())
                .surname(dto.getSurname())
                .authority(authorityService.findAuthorityByName("USER"))
                .enabled(true)
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .build();

        repository.save(newUser);
    }


    @Override
    public void deleteUser(String email) {
        User user = repository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        repository.delete(user);
    }

}
