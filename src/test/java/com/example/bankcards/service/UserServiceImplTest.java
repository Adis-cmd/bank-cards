package com.example.bankcards.service;

import com.example.bankcards.dto.AuthDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.Authority;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthorityService authorityService;

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_ShouldSaveUser() {
        AuthDto dto = new AuthDto();
        dto.setName("John");
        dto.setSurname("Doe");
        dto.setEmail("john.doe@example.com");
        dto.setPassword("password123");

        Authority authority = Authority.builder().name("USER").build();

        when(authorityService.findAuthorityByName("USER")).thenReturn(authority);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPassword");

        userService.register(dto);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(captor.capture());

        User savedUser = captor.getValue();
        assertEquals("John", savedUser.getName());
        assertEquals("Doe", savedUser.getSurname());
        assertEquals("john.doe@example.com", savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals(authority, savedUser.getAuthority());
        assertTrue(savedUser.getEnabled());
    }


    @Test
    void getAllUser_ShouldReturnPageOfUserDto() {
        User user1 = User.builder()
                .id(1L)
                .name("John")
                .surname("Doe")
                .email("john@example.com")
                .build();

        User user2 = User.builder()
                .id(2L)
                .name("Jane")
                .surname("Smith")
                .email("jane@example.com")
                .build();

        List<User> userList = Arrays.asList(user1, user2);
        Page<User> userPage = new PageImpl<>(userList);

        Pageable pageable = PageRequest.of(0, 10);
        when(userRepository.findAll(pageable)).thenReturn(userPage);

        Page<UserDto> result = userService.getAllUser(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals("John", result.getContent().get(0).getName());
        assertEquals("Jane", result.getContent().get(1).getName());
    }

    @Test
    void findUserByEmail_ShouldReturnUser() {
        User user = User.builder()
                .id(1L)
                .name("John")
                .surname("Doe")
                .email("john@example.com")
                .build();

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        User result = userService.findUserByEmail("john@example.com");

        assertNotNull(result);
        assertEquals("John", result.getName());
        assertEquals("Doe", result.getSurname());
    }

    @Test
    void findUserByEmail_ShouldThrowIfNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            userService.findUserByEmail("unknown@example.com");
        });
    }

    @Test
    void deleteUser_ShouldDeleteUser_WhenUserExists() {
        User user = User.builder()
                .id(1L)
                .email("john.doe@example.com")
                .build();

        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));

        userService.deleteUser("john.doe@example.com");

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void deleteUser_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(com.example.bankcards.exception.UserNotFoundException.class, () -> {
            userService.deleteUser("unknown@example.com");
        });

        verify(userRepository, never()).delete(any());
    }

}
