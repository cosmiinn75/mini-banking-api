package com.cosmin.mini_banking_api.Controller;

import com.cosmin.mini_banking_api.Dto.AccountRequest;
import com.cosmin.mini_banking_api.Dto.AccountResponse;
import com.cosmin.mini_banking_api.Service.AccountService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{account_number}")
    public AccountResponse getAccount(@PathVariable Integer account_number){
      return accountService.getAccount(account_number);
    }

    @GetMapping
    public List<AccountResponse> getAllAccounts(){
        return accountService.getAllAccounts();
    }

    @PostMapping
    public AccountResponse createAccount(@Valid @RequestBody AccountRequest accountRequest){
        return accountService.createAccount(accountRequest);
    }



}
