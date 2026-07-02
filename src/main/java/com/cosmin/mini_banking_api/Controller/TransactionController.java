package com.cosmin.mini_banking_api.Controller;

import com.cosmin.mini_banking_api.Dto.*;
import com.cosmin.mini_banking_api.Enum.TransactionType;
import com.cosmin.mini_banking_api.Service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;


@Validated
@RestController
@RequestMapping("/api/accounts")
@Tag(
        name = "Transactions",
        description = "Endpoints for deposits, withdrawals, transfers and transaction history"
)
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }


    @Operation(
            summary = "Get transaction history",
            description = "Returns filtered and paginated transactions for a specific bank account owned by the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transactions returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid filter or pagination parameters"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @GetMapping("/{account_number}/transactions")
    public PagedResponse<TransactionResponse> getAllTransactions(            @Parameter(description = "Account number of the user's bank account", example = "1")
                                                                                 @PathVariable Integer accountNumber,

                                                                             @Parameter(description = "Optional transaction type filter", example = "DEPOSIT")
                                                                                 @RequestParam(required = false) TransactionType type,

                                                                             @Parameter(description = "Optional minimum transaction amount filter", example = "100")
                                                                                 @RequestParam(required = false)
                                                                                 @PositiveOrZero(message = "Minimum amount cannot be negative")
                                                                                 BigDecimal minAmount,

                                                                             @Parameter(description = "Page number, starting from 0", example = "0")
                                                                                 @RequestParam(defaultValue = "0")
                                                                                 @Min(value = 0, message = "Page number cannot be negative")
                                                                                 Integer page,

                                                                             @Parameter(description = "Number of transactions per page", example = "20")
                                                                                 @RequestParam(defaultValue = "20")
                                                                                 @Min(value = 1, message = "Page size must be at least 1")
                                                                                 @Max(value = 50, message = "Page size cannot be greater than 50")
                                                                                 Integer size
                                                            ){
        return transactionService.getAllTransactions(accountNumber,type,minAmount,page,size);
    }


    @Operation(
            summary = "Deposit money",
            description = "Deposits money into a specific bank account owned by the authenticated user and creates a DEPOSIT transaction."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deposit completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid deposit amount"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @PostMapping("/{account_number}/deposit")
    public DepositResponse deposit(  @Parameter(description = "Account number where the money will be deposited", example = "1") @PathVariable Integer account_number, @Valid @RequestBody DepositRequest request){
        return transactionService.deposit(account_number,request);
    }

    @Operation(
            summary = "Withdraw money",
            description = "Withdraws money from a specific bank account owned by the authenticated user and creates a WITHDRAWAL transaction."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Withdrawal completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid withdrawal amount or insufficient funds"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @PostMapping("/{account_number}/withdraw")
    public WithdrawalResponse withdraw(  @Parameter(description = "Account number from which the money will be withdrawn", example = "1") @PathVariable Integer account_number , @Valid @RequestBody WithdrawalRequest request){
        return transactionService.withdrawal(account_number,request);
    }


    @Operation(
            summary = "Transfer money",
            description = "Transfers money from one account owned by the authenticated user to another user's account. Creates TRANSFER_OUT and TRANSFER_IN transactions."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transfer completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid transfer amount, insufficient funds, or transfer to the same account"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "404", description = "Source account, target user, or target account not found")
    })
    @PostMapping("/{account_number}/transfer")
    public TransferResponse transfer(  @Parameter(description = "Source account number owned by the authenticated user", example = "1") @PathVariable Integer account_number, @Valid @RequestBody TransferRequest request){
        return transactionService.transfer(account_number,request);
    }
}
