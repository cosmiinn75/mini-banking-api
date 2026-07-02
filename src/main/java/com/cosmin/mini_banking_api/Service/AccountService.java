package com.cosmin.mini_banking_api.Service;

import com.cosmin.mini_banking_api.Dto.*;
import com.cosmin.mini_banking_api.Exception.*;
import com.cosmin.mini_banking_api.Model.BankAccount;
import com.cosmin.mini_banking_api.Repository.BankAccountRepository;
import com.cosmin.mini_banking_api.Model.User;
import com.cosmin.mini_banking_api.Repository.UserRepository;
import jakarta.transaction.Transactional;




import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountService {
    private final BankAccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountService(BankAccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }


    public PagedResponse<AccountResponse> getAllAccountsFiltered(BigDecimal minBalance , Integer page , Integer size){

        Pageable pageable = PageRequest.of(page,size);

       Page<AccountResponse> pages = accountRepository.findFilteredAccount(pageable,getCurrentUsername(),minBalance)
                .map(this::fromAccountToResponse);

       return PagedResponse.from(pages);

    }

    public AccountResponse getAccount(Integer account_number){
        BankAccount account = accountRepository.findByUserUsernameAndAccountNumber(getCurrentUsername(),account_number)
                .orElseThrow(() -> new AccountNotFoundException("Account doesn't exist"));

        if (!account.isActive()) {
            throw new AccountNotActiveException("Account not active");
        }

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

        Integer number_of_accounts = currentUser.getNumberOfAccounts();

        BankAccount account = new BankAccount();
        account.setBalance(BigDecimal.ZERO);
        account.setUser(currentUser);
        account.setName(account_name);
        account.setAccountNumber(number_of_accounts+1);
        account.setActive(true);

        currentUser.setNumberOfAccounts(number_of_accounts+1);

        userRepository.save(currentUser);

        accountRepository.save(account);

        return new AccountResponse(username,number_of_accounts+1,account_name, account.getBalance(),account.isActive());
    }


    @Transactional
    public DeleteResponse deleteAccount(Integer accountNumber){

        BankAccount account = accountRepository.findByUserUsernameAndAccountNumber(getCurrentUsername(),accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        if(!account.isActive()) {
            throw new AccountNotActiveException("Account not active");
        }

        if(account.getBalance().compareTo(BigDecimal.ZERO) >0 ){
            throw new PositiveBalanceException("Balance must be 0 to close account");
        }
        account.setActive(false);
        accountRepository.save(account);

        return new DeleteResponse("Account closed successfully");
    }


    @Transactional
    public AccountResponse updateAccountName(Integer accountNumber , UpdateRequest request){

        BankAccount accountToUpdate = accountRepository.findByUserUsernameAndAccountNumber(getCurrentUsername(),accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        if(!accountToUpdate.isActive()) {
            throw new AccountNotActiveException("Account not active");
        }

        if(!accountToUpdate.getName().equals(request.newName()) &&
        accountRepository.existsByUserUsernameAndName(getCurrentUsername(), request.newName())) {
            throw new AccountNameAlreadyExistsException("Account name already exists");
        }
        accountToUpdate.setName(request.newName());
        accountRepository.save(accountToUpdate);
        return fromAccountToResponse(accountToUpdate);
    }

    private String getCurrentUsername(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }


    public AccountResponse fromAccountToResponse(BankAccount account){
        return new AccountResponse(account.getUser().getUsername(),account.getAccountNumber(),account.getName(),account.getBalance(),account.isActive());
    }






}
