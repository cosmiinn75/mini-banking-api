package com.cosmin.mini_banking_api.Controller;

import com.cosmin.mini_banking_api.Dto.*;
import com.cosmin.mini_banking_api.Enum.TransactionType;
import com.cosmin.mini_banking_api.Service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;



@RestController
@RequestMapping("/api/accounts")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/{account_number}/transactions")
    public List<TransactionResponse> getAllTransactions(@PathVariable Integer account_number,
                                                        @RequestParam(required = false) TransactionType type,
                                                        @RequestParam(required = false) BigDecimal amount
                                                        ){
        return transactionService.getAllTransactions(account_number,type,amount);
    }

    @PostMapping("/{account_number}/deposit")
    public DepositResponse deposit(@PathVariable Integer account_number, @Valid @RequestBody DepositRequest request){
        return transactionService.deposit(account_number,request);
    }

    @PostMapping("/{account_number}/withdraw")
    public WithdrawalResponse withdraw(@PathVariable Integer account_number , @Valid @RequestBody WithdrawalRequest request){
        return transactionService.withdrawal(account_number,request);
    }

    @PostMapping("/{account_number}/transfer")
    public TransferResponse transfer(@PathVariable Integer account_number, @Valid @RequestBody TransferRequest request){
        return transactionService.transfer(account_number,request);
    }
}
