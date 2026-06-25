package com.cosmin.mini_banking_api.Dto;

import jakarta.validation.constraints.NotBlank;

public record AccountRequest(
        @NotBlank
        String name
) {
}
