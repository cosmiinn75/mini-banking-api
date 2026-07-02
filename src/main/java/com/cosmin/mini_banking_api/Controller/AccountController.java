package com.cosmin.mini_banking_api.Controller;

import com.cosmin.mini_banking_api.Dto.*;
import com.cosmin.mini_banking_api.Service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Tag(
        name = "Accounts",
        description = "Endpoints for creating, viewing, filtering and updating bank accounts"
)
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }


    @Operation(
            summary = "Get account by account number",
            description = "Returns a specific bank account owned by the currently authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account returned successfully"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @GetMapping("/{account_number}")
    public AccountResponse getAccount(@PathVariable Integer account_number){
      return accountService.getAccount(account_number);
    }


    @Operation(
            summary = "Get all accounts",
            description = "Returns the authenticated user's bank accounts. Supports optional minimum balance filtering and pagination."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accounts returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid filter or pagination parameters"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token")
    })
    @GetMapping
    public PagedResponse<AccountResponse> getAllAccounts(@RequestParam(required = false)
                                                             @PositiveOrZero
                                                             BigDecimal minBalance,

                                                         @Min(value = 0, message = "Page number cannot be negative")
                                                         @RequestParam(value = "page" , defaultValue = "0")
                                                         Integer page,

                                                             @Min(value = 1, message = "Page size must be at least 1")
                                                             @Max(value = 50, message = "Page size cannot be greater than 50")
                                                             @RequestParam(value = "size" , defaultValue = "20")
                                                             Integer size
    ){
        return accountService.getAllAccountsFiltered(minBalance,page,size);
    }


    @Operation(
            summary = "Create a new account",
            description = "Creates a new bank account for the currently authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Account created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "409", description = "Account name already exists")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public AccountResponse createAccount(@Valid @RequestBody AccountRequest accountRequest){
        return accountService.createAccount(accountRequest);
    }

    @Operation(
            summary = "Update account name",
            description = "Updates the name of a specific bank account owned by the currently authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "409", description = "Account name already exists")
    })
    @PutMapping("/{accountNumber}")
    public AccountResponse updateAccount(@PathVariable Integer accountNumber , @Valid @RequestBody UpdateRequest request){

        return accountService.updateAccountName(accountNumber,request);
    }
    @Operation(
            summary = "Delete account",
            description = "Closes a specific bank account owned by the authenticated user. The account can only be closed if it exists and has zero balance."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Account cannot be deleted because it does not meet the required conditions"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @DeleteMapping("/{accountNumber}")
    public DeleteResponse deleteAccount(@PathVariable Integer accountNumber) {
        return accountService.deleteAccount(accountNumber);
    }

}
