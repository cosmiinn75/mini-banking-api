package com.cosmin.mini_banking_api.Service;

import com.cosmin.mini_banking_api.Dto.AccountRequest;
import com.cosmin.mini_banking_api.Dto.AccountResponse;
import com.cosmin.mini_banking_api.Enum.Role;
import com.cosmin.mini_banking_api.Exception.AccountNameAlreadyExistsException;
import com.cosmin.mini_banking_api.Exception.AccountNotFoundException;
import com.cosmin.mini_banking_api.Exception.InvalidCredentialsException;
import com.cosmin.mini_banking_api.Model.BankAccount;
import com.cosmin.mini_banking_api.Repository.BankAccountRepository;
import com.cosmin.mini_banking_api.Repository.UserRepository;
import com.cosmin.mini_banking_api.Model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;


import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private BankAccountRepository accountRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AccountService accountService;


    @AfterEach
    void tearDown(){
        SecurityContextHolder.clearContext();
    }



    @Test
    void createAccount_shouldCreateAccountForAuthenticatedUser() {
        mockAuthenticatedUser("cosmin");

        AccountRequest request = new AccountRequest("Savings");

        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setPassword("parola");
        currentUser.setUsername("cosmin");
        currentUser.setNumberOfAccounts(1);

        when(userRepository.findByUsername("cosmin")).thenReturn(Optional.of(currentUser));

        when(accountRepository.existsByUserUsernameAndName("cosmin", "Savings")).thenReturn(false);

        AccountResponse response = accountService.createAccount(request);

        assertEquals("Savings",response.name());
        assertEquals("cosmin",response.username());
        assertEquals(2,response.accountNumber());
        assertEquals(BigDecimal.ZERO,response.balance());

        verify(userRepository).save(currentUser);
        verify(accountRepository).save(any(BankAccount.class));

    }

    @Test
    void createAccount_shouldThrowException_whenAccountNameAlreadyExists(){
        mockAuthenticatedUser("cosmin");
        AccountRequest request = new AccountRequest("Savings");
        User currentUser = new User();
        currentUser.setNumberOfAccounts(1);
        currentUser.setUsername("cosmin");

        when(userRepository.findByUsername("cosmin")).thenReturn(Optional.of(currentUser));

        when(accountRepository.existsByUserUsernameAndName("cosmin","Savings")).thenReturn(true);

        assertThrows(AccountNameAlreadyExistsException.class,
                () -> accountService.createAccount(request));

        verify(userRepository,never()).save(any(User.class));
        verify(accountRepository,never()).save(any(BankAccount.class));

    }

    @Test
    void createAccount_shouldThrowException_whenCurrentUserNotFound(){
        mockAuthenticatedUser("cosmin");
        AccountRequest request = new AccountRequest("Savings");

        when(userRepository.findByUsername("cosmin")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class , () -> accountService.createAccount(request));
        verify(userRepository,never()).save(any(User.class));
        verifyNoInteractions(accountRepository);

    }


    @Test
    void getAllAccounts_shouldReturnAccountsForAuthenticatedUser(){




        mockAuthenticatedUser("cosmin");
        User currentUser = new User();
        currentUser.setRole(Role.CUSTOMER);
        currentUser.setNumberOfAccounts(2);
        currentUser.setUsername("cosmin");
        currentUser.setId(1L);

        BankAccount bankAccount1 = new BankAccount();
        bankAccount1.setName("Main");
        bankAccount1.setAccountNumber(1);
        bankAccount1.setUser(currentUser);
        bankAccount1.setBalance(BigDecimal.valueOf(100));

        BankAccount bankAccount2 = new BankAccount();
        bankAccount2.setName("Savings");
        bankAccount2.setAccountNumber(2);
        bankAccount2.setUser(currentUser);
        bankAccount2.setBalance(BigDecimal.ZERO);
        Pageable pageable = PageRequest.of(0,20);
        Page<BankAccount> accountPage = new PageImpl<>(
                List.of(bankAccount1,bankAccount2),
                pageable,
                2
        );
        when(accountRepository.findFilteredAccount(pageable,"cosmin" , BigDecimal.ZERO)).thenReturn(
            accountPage
        );


    Page<AccountResponse> responses = accountService.getAllAccountsFiltered(BigDecimal.ZERO,0,20);

    AccountResponse first = responses.getContent().get(0);
    AccountResponse second = responses.getContent().get(1);
        assertEquals(2,responses.getContent().size());

        assertEquals("cosmin", first.username());
        assertEquals(1, first.accountNumber());
        assertEquals("Main", first.name());
        assertEquals(BigDecimal.valueOf(100), first.balance());

        assertEquals("cosmin", second.username());
        assertEquals(2, second.accountNumber());
        assertEquals("Savings", second.name());
        assertEquals(BigDecimal.ZERO, second.balance());

        verify(accountRepository).findFilteredAccount(pageable,"cosmin" ,BigDecimal.ZERO);
    }


    @Test
    void getAccount_shouldReturnAccount(){
        mockAuthenticatedUser("cosmin");

        Integer accountNumber = 1;
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("cosmin");
        currentUser.setRole(Role.CUSTOMER);
        currentUser.setNumberOfAccounts(1);

        BankAccount account = new BankAccount();
        account.setAccountNumber(accountNumber);
        account.setUser(currentUser);
        account.setName("Main");
        account.setBalance(BigDecimal.ZERO);

        when(accountRepository.findByUserUsernameAndAccountNumber("cosmin",accountNumber))
                .thenReturn(Optional.of(account));

        AccountResponse response = accountService.getAccount(accountNumber);

        assertEquals(accountNumber,response.accountNumber());
        assertEquals(BigDecimal.ZERO,response.balance());
        assertEquals("Main",response.name());
        assertEquals("cosmin",response.username());

        verify(accountRepository).findByUserUsernameAndAccountNumber("cosmin",accountNumber);
    }


    @Test
    void getAccount_shouldThrowException_whenAccountDoesNotExist(){
        mockAuthenticatedUser("cosmin");
        Integer accountNumber = 1;

        when(accountRepository.findByUserUsernameAndAccountNumber("cosmin",accountNumber))
                .thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class
        , () -> accountService.getAccount(accountNumber));

        verify(accountRepository).findByUserUsernameAndAccountNumber("cosmin", accountNumber);

    }


    private void mockAuthenticatedUser(String username){
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username,null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
