package com.cosmin.mini_banking_api.Service;

import com.cosmin.mini_banking_api.Dto.*;
import com.cosmin.mini_banking_api.Enum.Role;
import com.cosmin.mini_banking_api.Model.BankAccount;
import com.cosmin.mini_banking_api.Model.User;
import com.cosmin.mini_banking_api.Repository.BankAccountRepository;
import com.cosmin.mini_banking_api.Repository.UserRepository;
import com.cosmin.mini_banking_api.Security.JWTUtil;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BankAccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

    public AuthService(UserRepository userRepository, BankAccountRepository accountRepository, PasswordEncoder passwordEncoder, JWTUtil jwtUtil) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse loginUser(LoginRequest request){
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if(!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        return new AuthResponse(jwtUtil.generateToken(user.getUsername(), user.getRole()));
    }

    @Transactional
    public AuthResponse registerUser(AuthRequest request){
        Optional<User> user = userRepository.findByUsername(request.username());

        if(user.isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        if(userRepository.existsByEmail(request.email())){
            throw new RuntimeException("Email already exists");
        }

        User currentUser = new User();
        currentUser.setEmail(request.email());
        currentUser.setPassword(passwordEncoder.encode(request.password()));
        currentUser.setRole(Role.CUSTOMER);
        currentUser.setUsername(request.username());
        currentUser.setNumber_of_accounts(1);

        User savedUser = userRepository.save(currentUser);

        BankAccount bankAccount = new BankAccount();
        bankAccount.setBalance(BigDecimal.ZERO);
        bankAccount.setUser(savedUser);
        bankAccount.setName("Main Account");
        bankAccount.setAccount_number(1);

        accountRepository.save(bankAccount);
        return new AuthResponse(jwtUtil.generateToken(savedUser.getUsername(), savedUser.getRole()));
    }



}
