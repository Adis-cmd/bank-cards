package com.example.bankcards.service;

import com.example.bankcards.entity.Authority;
import com.example.bankcards.exception.AuthorityNotFoundException;
import com.example.bankcards.repository.AuthorityRepository;
import com.example.bankcards.service.impl.AuthorityServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthorityServiceImplTest {

    @InjectMocks
    private AuthorityServiceImpl authorityService;

    @Mock
    private AuthorityRepository authorityRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAuthorityByName_ShouldReturnAuthority() {
        Authority role = new Authority();
        role.setName("USER");

        when(authorityRepository.findAuthorityByName("USER")).thenReturn(Optional.of(role));

        Authority result = authorityService.findAuthorityByName("USER");

        assertEquals("USER", result.getName());
    }

    @Test
    void findAuthorityByName_ShouldThrowException() {
        when(authorityRepository.findAuthorityByName("ADMIN")).thenReturn(Optional.empty());

        assertThrows(AuthorityNotFoundException.class,
                () -> authorityService.findAuthorityByName("ADMIN"));
    }
}
