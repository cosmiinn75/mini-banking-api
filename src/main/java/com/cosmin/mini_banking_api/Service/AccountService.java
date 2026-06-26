package com.cosmin.mini_banking_api.Service;

import com.cosmin.mini_banking_api.Exception.*;
import com.cosmin.mini_banking_api.Model.BankAccount;
import com.cosmin.mini_banking_api.Repository.BankAccountRepository;
import com.cosmin.mini_banking_api.Dto.AccountRequest;
import com.cosmin.mini_banking_api.Dto.AccountResponse;
import com.cosmin.mini_banking_api.Model.User;
import com.cosmin.mini_banking_api.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {
    private final BankAccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountService(BankAccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }


    public List<AccountResponse> getAllAccounts(){
        return accountRepository.getAllByUserUsername(getCurrentUsername())
                .stream()
                .map(this::fromAccountToResponse)
                .toList();
    }

    public AccountResponse getAccount(Integer account_number){
        BankAccount account = accountRepository.findByUserUsernameAndAccountNumber(getCurrentUsername(),account_number)
                .orElseThrow(() -> new AccountNotFoundException("Account doesn't exist"));
        return fromAccountToResponse(account);
    }

    @Transactional
    public AccountResponse createAccount(AccountRequest request){
        String username = getCurrentUsername();
        User currentUser =  userRepository.findByUsername(username).
                orElseThrow(() -> new InvalidCredentialsException("Invalid username")); // Invalid username

        String account_name = request.name();

        if(accountRepository.existsByUserUsernameAndName(username,account_name)){
            throw new AccountNameAlreadyExistsException("Account name already exists");
        }

        Integer number_of_accounts = currentUser.getNumber_of_accounts();

        BankAccount account = new BankAccount();
        account.setBalance(BigDecimal.ZERO);
        account.setUser(currentUser);
        account.setName(account_name);
        account.setAccountNumber(number_of_accounts+1);

        currentUser.setNumber_of_accounts(number_of_accounts+1);

        userRepository.save(currentUser);

        accountRepository.save(account);

        return new AccountResponse(username,number_of_accounts+1,account_name, account.getBalance());
    }

    private String getCurrentUsername(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }


    public AccountResponse fromAccountToResponse(BankAccount account){
        return new AccountResponse(account.getUser().getUsername(),account.getAccountNumber(),account.getName(),account.getBalance());
    }
}
