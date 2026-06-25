package com.cosmin.mini_banking_api.Dto;

import com.cosmin.mini_banking_api.Enum.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChangeRoleRequest(
        @NotNull(message = "Role is required")
        Role role
) {
}
