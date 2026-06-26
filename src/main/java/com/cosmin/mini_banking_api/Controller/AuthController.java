package com.cosmin.mini_banking_api.Controller;

import com.cosmin.mini_banking_api.Dto.AuthRequest;
import com.cosmin.mini_banking_api.Dto.AuthResponse;
import com.cosmin.mini_banking_api.Dto.LoginRequest;
import com.cosmin.mini_banking_api.Service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody AuthRequest request){
        return authService.registerUser(request);
    }


    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request){
        return authService.loginUser(request);
    }

}
