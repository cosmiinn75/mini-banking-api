package com.cosmin.mini_banking_api.Dto;

import com.cosmin.mini_banking_api.Enum.Role;

public record UserResponse(
        Long id,
        String username,
        Role role
) {
}
