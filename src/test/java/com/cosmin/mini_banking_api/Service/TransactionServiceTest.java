package com.cosmin.mini_banking_api.Service;

import com.cosmin.mini_banking_api.Dto.*;
import com.cosmin.mini_banking_api.Enum.Role;
import com.cosmin.mini_banking_api.Enum.TransactionType;
import com.cosmin.mini_banking_api.Exception.AccountNotFoundException;
import com.cosmin.mini_banking_api.Exception.CantTransferToOwnAccountException;
import com.cosmin.mini_banking_api.Exception.InsufficientFundsException;
import com.cosmin.mini_banking_api.Model.BankAccount;
import com.cosmin.mini_banking_api.Model.Transaction;
import com.cosmin.mini_banking_api.Repository.BankAccountRepository;
import com.cosmin.mini_banking_api.Repository.TransactionRepository;
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
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private BankAccountRepository accountRepository;
    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    TransactionService transactionService;

    @AfterEach
    void tearDown(){
        SecurityContextHolder.clearContext();
    }


    @Test
    void deposit_shouldReturnDepositResponse(){
        mockAuthenticatedUser("cosmin");
        User currentUser = new User();
        currentUser.setNumberOfAccounts(1);
        currentUser.setRole(Role.CUSTOMER);
        currentUser.setUsername("cosmin");

        Integer accountNumber = 1;
        DepositRequest request = new DepositRequest(BigDecimal.valueOf(100));

        BankAccount account = new BankAccount();
        account.setBalance(BigDecimal.ZERO);
        account.setUser(currentUser);
        account.setAccountNumber(1);
        account.setName("Main");

        when(accountRepository.findByUserUsernameAndAccountNumber("cosmin",accountNumber))
                .thenReturn(Optional.of(account));


        DepositResponse response = transactionService.deposit(accountNumber,request);

        assertEquals("Main",response.account_name());
        assertEquals(1,response.account_number());
        assertEquals(BigDecimal.valueOf(100),response.balance());

        verify(transactionRepository).save(any(Transaction.class));
        verify(accountRepository).save(any(BankAccount.class));

    }

    @Test
    void deposit_shouldThrowException_whenAccountDoesNotExist(){
        mockAuthenticatedUser("cosmin");
        Integer account_number = 1;
        DepositRequest request = new DepositRequest(BigDecimal.valueOf(100));
        when(accountRepository.findByUserUsernameAndAccountNumber("cosmin",account_number))
                .thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class
        , () -> transactionService.deposit(account_number,request));


        verify(accountRepository).findByUserUsernameAndAccountNumber("cosmin", account_number);
        verifyNoMoreInteractions(accountRepository);
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void withdraw_shouldLowerBalanceAndReturnWithdrawalResponse(){
        mockAuthenticatedUser("cosmin");
        Integer accountNumber = 1;
        WithdrawalRequest request = new WithdrawalRequest(BigDecimal.valueOf(100));

        BankAccount account = new BankAccount();

        account.setBalance(BigDecimal.valueOf(100));
        account.setName("Main");
        account.setAccountNumber(1);
        when(accountRepository.findByUserUsernameAndAccountNumber("cosmin",1))
                .thenReturn(Optional.of(account));
        WithdrawalResponse response = transactionService.withdrawal(accountNumber,request);

        assertEquals("Main",response.account_name());
        assertEquals(BigDecimal.ZERO,response.balance());
        assertEquals(1,response.account_number());

        verify(transactionRepository).save(any(Transaction.class));
        verify(accountRepository).save(any(BankAccount.class));
    }

    @Test
    void withdraw_shouldThrowException_whenAccountDoesNotExist(){
        mockAuthenticatedUser("cosmin");
        Integer accountNumber = 1;
        WithdrawalRequest request = new WithdrawalRequest(BigDecimal.valueOf(100));

        when(accountRepository.findByUserUsernameAndAccountNumber("cosmin",1))
                .thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> transactionService.withdrawal(accountNumber,request));

        verify(accountRepository,never()).save(any(BankAccount.class));
        verify(transactionRepository,never()).save(any(Transaction.class));

    }

    @Test
    void withdraw_shouldThrowException_whenInsufficientFunds(){
        mockAuthenticatedUser("cosmin");
        Integer accountNumber = 1;
        WithdrawalRequest request = new WithdrawalRequest(BigDecimal.valueOf(200));

        BankAccount account = new BankAccount();

        account.setBalance(BigDecimal.valueOf(100));
        account.setName("Main");
        account.setAccountNumber(1);
        when(accountRepository.findByUserUsernameAndAccountNumber("cosmin",1))
                .thenReturn(Optional.of(account));

        assertThrows(InsufficientFundsException.class,
        () -> transactionService.withdrawal(accountNumber,request));

        verify(accountRepository).findByUserUsernameAndAccountNumber("cosmin", accountNumber);
        verify(accountRepository,never()).save(any(BankAccount.class));
        verify(transactionRepository,never()).save(any(Transaction.class));
    }


    @Test
    void getTransactions_shouldReturnTransactionsForAccount() {
        mockAuthenticatedUser("cosmin");

        Integer accountNumber = 1;

        BankAccount account = new BankAccount();
        account.setAccountNumber(1);
        account.setBalance(BigDecimal.ZERO);
        account.setName("Main");

        when(accountRepository.findByUserUsernameAndAccountNumber("cosmin", accountNumber))
                .thenReturn(Optional.of(account));

        LocalDateTime time1 = LocalDateTime.of(2026, 6, 26, 10, 0);
        LocalDateTime time2 = LocalDateTime.of(2026, 6, 26, 11, 0);

        Transaction transaction1 = new Transaction();
        transaction1.setBankAccount(account);
        transaction1.setTransactionType(TransactionType.DEPOSIT);
        transaction1.setCreatedAt(time1);
        transaction1.setId(1L);
        transaction1.setAmount(BigDecimal.ONE);

        Transaction transaction2 = new Transaction();
        transaction2.setBankAccount(account);
        transaction2.setTransactionType(TransactionType.WITHDRAWAL);
        transaction2.setCreatedAt(time2);
        transaction2.setId(2L);
        transaction2.setAmount(BigDecimal.valueOf(100));

        Pageable pageable = PageRequest.of(0,20);
        Page<Transaction> pages = new PageImpl<>(
                List.of(transaction1,transaction2),
                pageable,
                2
        );

        when(transactionRepository.findFilteredTransactions(account,TransactionType.TRANSFER_IN , BigDecimal.ZERO , pageable))
                .thenReturn(pages);

        Page<TransactionResponse> response = transactionService.getAllTransactions(accountNumber,TransactionType.TRANSFER_IN ,BigDecimal.ZERO , 0,20);

        TransactionResponse first = response.getContent().get(0);
        TransactionResponse second = response.getContent().get(1);

        assertEquals(2, response.getContent().size());


        assertEquals(1, first.accountNumber());
        assertEquals("Main", first.accountName());
        assertEquals(TransactionType.DEPOSIT, first.transactionType());
        assertEquals(BigDecimal.ONE, first.amount());
        assertEquals(time1, first.createdAt());


        assertEquals(1, second.accountNumber());
        assertEquals("Main", second.accountName());
        assertEquals(TransactionType.WITHDRAWAL, second.transactionType());
        assertEquals(BigDecimal.valueOf(100),second.amount());
        assertEquals(time2, second.createdAt());

        verify(accountRepository).findByUserUsernameAndAccountNumber("cosmin", accountNumber);
        verify(transactionRepository).findFilteredTransactions(account,TransactionType.TRANSFER_IN,BigDecimal.ZERO,pageable);
    }

    @Test
    void getTransactions_shouldThrowException_whenAccountDoesNotExist() {
        mockAuthenticatedUser("cosmin");

        Integer accountNumber = 1;

        when(accountRepository.findByUserUsernameAndAccountNumber("cosmin", accountNumber))
                .thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> transactionService.getAllTransactions(accountNumber,TransactionType.TRANSFER_IN,BigDecimal.ZERO,0,20));

        verify(accountRepository)
                .findByUserUsernameAndAccountNumber("cosmin", accountNumber);

        verifyNoInteractions(transactionRepository);
    }

    @Test
    void transfer_shouldMoveMoneyAndReturnTransferResponse(){
        mockAuthenticatedUser("cosmin");

        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("cosmin");
        currentUser.setNumberOfAccounts(1);
        currentUser.setRole(Role.CUSTOMER);

        User toUser = new User();
        toUser.setId(2L);
        toUser.setUsername("ionut");
        toUser.setNumberOfAccounts(2);
        toUser.setRole(Role.CUSTOMER);

        TransferRequest request = new TransferRequest(
                "ionut"
                ,
                1,
                 BigDecimal.valueOf(100)
        );



        BankAccount fromAccount = new BankAccount();
        fromAccount.setName("Main");
        fromAccount.setBalance(BigDecimal.valueOf(500));
        fromAccount.setAccountNumber(1);
        fromAccount.setUser(currentUser);
        fromAccount.setId(1L);

        BankAccount toAccount = new BankAccount();
        toAccount.setName("Main");
        toAccount.setBalance(BigDecimal.valueOf(50));
        toAccount.setAccountNumber(1);
        toAccount.setUser(toUser);
        toAccount.setId(1L);

        when(accountRepository.findByUserUsernameAndAccountNumber("cosmin",1))
                .thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByUserUsernameAndAccountNumber("ionut",request.toAccountNumber()))
                .thenReturn(Optional.of(toAccount));

        TransferResponse response = transactionService.transfer(1,request);


        assertEquals("cosmin", response.fromUsername());
        assertEquals(1, response.fromAccountNumber());
        assertEquals("Main", response.fromAccountName());
        assertEquals(BigDecimal.valueOf(400), response.fromBalanceAfter());

        assertEquals("ionut", response.toUsername());
        assertEquals(1, response.toAccountNumber());
        assertEquals("Main", response.toAccountName());
        assertEquals(BigDecimal.valueOf(150), response.toBalanceAfter());

        assertEquals(BigDecimal.valueOf(100), response.amount());
        assertEquals("Transfer completed successfully", response.message());

        assertEquals(BigDecimal.valueOf(400), fromAccount.getBalance());
        assertEquals(BigDecimal.valueOf(150), toAccount.getBalance());

        verify(accountRepository).findByUserUsernameAndAccountNumber("cosmin", 1);
        verify(accountRepository).findByUserUsernameAndAccountNumber("ionut", 1);

        verify(accountRepository).save(fromAccount);
        verify(accountRepository).save(toAccount);

        verify(transactionRepository, times(2)).save(any(Transaction.class));

    }


    @Test
    void transfer_shouldThrowException_whenSourceAccountDoesNotExist(){
        mockAuthenticatedUser("cosmin");
        when(accountRepository.findByUserUsernameAndAccountNumber("cosmin",1))
                .thenReturn(Optional.empty());
        TransferRequest request = new TransferRequest(
                "ionut"
                ,
                1,
                BigDecimal.valueOf(100)
        );

        assertThrows(AccountNotFoundException.class,
                () -> transactionService.transfer(1,request));

        verify(accountRepository)
                .findByUserUsernameAndAccountNumber("cosmin", 1);

        verify(accountRepository, never())
                .findByUserUsernameAndAccountNumber("ionut", 1);

        verify(accountRepository, never())
                .save(any(BankAccount.class));

        verifyNoInteractions(transactionRepository);
    }

    @Test
    void transfer_shouldThrowException_whenDestinationAccountDoesNotExist(){
        mockAuthenticatedUser("cosmin");

        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("cosmin");
        currentUser.setNumberOfAccounts(1);
        currentUser.setRole(Role.CUSTOMER);

        TransferRequest request = new TransferRequest(
                "ionut"
                ,
                1,
                BigDecimal.valueOf(100)
        );

        BankAccount fromAccount = new BankAccount();
        fromAccount.setName("Main");
        fromAccount.setBalance(BigDecimal.valueOf(500));
        fromAccount.setAccountNumber(1);
        fromAccount.setUser(currentUser);
        fromAccount.setId(1L);


        when(accountRepository.findByUserUsernameAndAccountNumber("cosmin",1))
                .thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByUserUsernameAndAccountNumber("ionut",request.toAccountNumber()))
                .thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> transactionService.transfer(1,request));

        verify(accountRepository)
                .findByUserUsernameAndAccountNumber("cosmin", 1);

        verify(accountRepository)
                .findByUserUsernameAndAccountNumber("ionut", request.toAccountNumber());

        verify(accountRepository, never())
                .save(any(BankAccount.class));

        verifyNoInteractions(transactionRepository);

        assertEquals(BigDecimal.valueOf(500), fromAccount.getBalance());
    }

    @Test
    void transfer_shouldThrowException_whenTransferingToSameAccount() {
        mockAuthenticatedUser("cosmin");

        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("cosmin");
        currentUser.setNumberOfAccounts(1);
        currentUser.setRole(Role.CUSTOMER);

        TransferRequest request = new TransferRequest(
                "cosmin",
                1,
                BigDecimal.valueOf(100)
        );

        BankAccount fromAccount = new BankAccount();
        fromAccount.setName("Main");
        fromAccount.setBalance(BigDecimal.valueOf(500));
        fromAccount.setAccountNumber(1);
        fromAccount.setUser(currentUser);
        fromAccount.setId(1L);

        when(accountRepository.findByUserUsernameAndAccountNumber("cosmin", 1))
                .thenReturn(Optional.of(fromAccount));

        assertThrows(CantTransferToOwnAccountException.class,
                () -> transactionService.transfer(1, request));

        verify(accountRepository, times(2))
                .findByUserUsernameAndAccountNumber("cosmin", 1);

        verify(accountRepository, never())
                .save(any(BankAccount.class));

        verifyNoInteractions(transactionRepository);

        assertEquals(BigDecimal.valueOf(500), fromAccount.getBalance());
    }

    @Test
    void transfer_shouldThrowException_whenInsufficientFunds() {
        mockAuthenticatedUser("cosmin");

        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("cosmin");
        currentUser.setNumberOfAccounts(1);
        currentUser.setRole(Role.CUSTOMER);

        User toUser = new User();
        toUser.setId(2L);
        toUser.setUsername("ionut");
        toUser.setNumberOfAccounts(1);
        toUser.setRole(Role.CUSTOMER);

        TransferRequest request = new TransferRequest(
                "ionut",
                1,
                BigDecimal.valueOf(100)
        );

        BankAccount fromAccount = new BankAccount();
        fromAccount.setName("Main");
        fromAccount.setBalance(BigDecimal.valueOf(50));
        fromAccount.setAccountNumber(1);
        fromAccount.setUser(currentUser);
        fromAccount.setId(1L);

        BankAccount toAccount = new BankAccount();
        toAccount.setName("Main");
        toAccount.setBalance(BigDecimal.valueOf(200));
        toAccount.setAccountNumber(1);
        toAccount.setUser(toUser);
        toAccount.setId(2L);

        when(accountRepository.findByUserUsernameAndAccountNumber("cosmin", 1))
                .thenReturn(Optional.of(fromAccount));

        when(accountRepository.findByUserUsernameAndAccountNumber("ionut", 1))
                .thenReturn(Optional.of(toAccount));

        assertThrows(InsufficientFundsException.class,
                () -> transactionService.transfer(1, request));

        verify(accountRepository)
                .findByUserUsernameAndAccountNumber("cosmin", 1);

        verify(accountRepository)
                .findByUserUsernameAndAccountNumber("ionut", 1);

        verify(accountRepository, never())
                .save(any(BankAccount.class));

        verifyNoInteractions(transactionRepository);

        assertEquals(BigDecimal.valueOf(50), fromAccount.getBalance());
        assertEquals(BigDecimal.valueOf(200), toAccount.getBalance());
    }




    private void mockAuthenticatedUser(String username){
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(username,null, Collections.emptyList());

        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
