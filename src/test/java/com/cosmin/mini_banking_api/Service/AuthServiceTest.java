package com.cosmin.mini_banking_api.Service;

import com.cosmin.mini_banking_api.Dto.AuthRequest;
import com.cosmin.mini_banking_api.Dto.AuthResponse;
import com.cosmin.mini_banking_api.Dto.LoginRequest;
import com.cosmin.mini_banking_api.Enum.Role;
import com.cosmin.mini_banking_api.Exception.EmailAlreadyExistsException;
import com.cosmin.mini_banking_api.Exception.InvalidCredentialsException;
import com.cosmin.mini_banking_api.Exception.UsernameAlreadyExistsException;
import com.cosmin.mini_banking_api.Model.BankAccount;
import com.cosmin.mini_banking_api.Model.User;
import com.cosmin.mini_banking_api.Repository.BankAccountRepository;
import com.cosmin.mini_banking_api.Repository.UserRepository;
import com.cosmin.mini_banking_api.Security.JWTUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BankAccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JWTUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerUser_shouldCreateAccount(){

        AuthRequest request = new AuthRequest(
                "cosmin",
                "cosmin@gmail.com",
                "parola"

        );
        when(userRepository.findByUsername("cosmin")).thenReturn(Optional.empty());
        when(userRepository.existsByEmail("cosmin@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("parola")).thenReturn("encodedPassword");


        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("cosmin");
        savedUser.setEmail("cosmin@gmail.com");
        savedUser.setPassword("encodedPassword");
        savedUser.setRole(Role.CUSTOMER);
        savedUser.setNumber_of_accounts(1);

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        when(jwtUtil.generateToken("cosmin",Role.CUSTOMER))
                .thenReturn("fake-token");

        AuthResponse response = authService.registerUser(request);

        assertEquals("fake-token",response.token());

        verify(userRepository).save(any(User.class));
        verify(accountRepository).save(any(BankAccount.class));

    }

    @Test
    void registerUser_shouldThrowException_whenUsernameAlreadyExists(){
        AuthRequest request = new AuthRequest(
                "cosmin",
                        "cosmin@email.com",
                "parola"
        );

        User existingUser = new User();
        existingUser.setUsername("cosmin");

        when(userRepository.findByUsername("cosmin")).thenReturn(Optional.of(existingUser));

        assertThrows(UsernameAlreadyExistsException.class , () -> authService.registerUser(request));


        verify(userRepository,never()).save(any(User.class));
        verify(accountRepository,never()).save(any(BankAccount.class));
    }


    @Test
    void registerUser_shouldThrowException_whenEmailExists(){
        AuthRequest request = new AuthRequest(
                "cosmin",
                        "cosmin@gmail.com",
                        "parola"
        );
        when(userRepository.findByUsername("cosmin")).thenReturn(Optional.empty());
        when(userRepository.existsByEmail("cosmin@gmail.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class,
                () -> authService.registerUser(request));

        verify(userRepository,never()).save(any(User.class));
        verify(accountRepository,never()).save(any(BankAccount.class));
    }

    @Test
    void shouldReturnToken_whenCredentialsAreValid(){

        LoginRequest request = new LoginRequest(
                "cosmin",
                "parola"
        );
        User existingUser = new User();
        existingUser.setUsername("cosmin");
        existingUser.setPassword("fakePassword");
        existingUser.setRole(Role.CUSTOMER);
        existingUser.setId(1L);
        when(userRepository.findByUsername("cosmin")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("parola", existingUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken("cosmin",Role.CUSTOMER)).thenReturn("fake-token");
        AuthResponse response = authService.loginUser(request);

        assertEquals("fake-token",response.token());
        verify(jwtUtil).generateToken("cosmin",Role.CUSTOMER);

    }

    @Test
    void loginUser_shouldThrowException_whenPasswordIsInvalid(){
        LoginRequest request = new LoginRequest(
                "cosmin",
                "parola"
        );

        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("cosmin");
        currentUser.setPassword("fakePassword");
        currentUser.setRole(Role.CUSTOMER);

        when(userRepository.findByUsername("cosmin")).thenReturn(Optional.of(currentUser));
        when(passwordEncoder.matches("parola",currentUser.getPassword())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> authService.loginUser(request));

     verifyNoInteractions(jwtUtil);

    }

    @Test
    void loginUser_shouldThrowException_whenUsernameIsInvalid(){
        LoginRequest request = new LoginRequest(
                "cosmin",
                "parola"
        );
        when(userRepository.findByUsername("cosmin")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class,
                () -> authService.loginUser(request));

        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtUtil);

    }

}
