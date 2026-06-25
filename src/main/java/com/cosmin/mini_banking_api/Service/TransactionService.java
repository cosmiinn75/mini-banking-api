package com.cosmin.mini_banking_api.Service;

import com.cosmin.mini_banking_api.Dto.*;
import com.cosmin.mini_banking_api.Enum.TransactionType;
import com.cosmin.mini_banking_api.Model.BankAccount;
import com.cosmin.mini_banking_api.Model.Transaction;
import com.cosmin.mini_banking_api.Repository.BankAccountRepository;
import com.cosmin.mini_banking_api.Repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {

    private final BankAccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(BankAccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public DepositResponse deposit(Integer account_number, DepositRequest request){
        String currentUsername = getCurrentUsername();
        BankAccount account = accountRepository.findByUserUsernameAndAccount_number(currentUsername,account_number)
                .orElseThrow(() -> new RuntimeException("Account doesn't exist"));
        BigDecimal amount = request.amount();
        account.setBalance(account.getBalance().add(amount));
        Transaction transaction = new Transaction();
        transaction.setBankAccount(account);
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transactionRepository.save(transaction);
        accountRepository.save(account);
        return new DepositResponse(account.getAccount_number(),account.getName(),account.getBalance());
    }

    @Transactional
    public WithdrawalResponse withdrawal(Integer account_number, WithdrawalRequest request){
        String currentUsername = getCurrentUsername();
        BankAccount account = accountRepository.findByUserUsernameAndAccount_number(currentUsername,account_number)
                .orElseThrow(() -> new RuntimeException("Account doesn't exist"));
        BigDecimal balance = account.getBalance();
        if(balance.compareTo(request.amount()) < 0){
            throw new RuntimeException("Insufficient funds");
        }
        account.setBalance(balance.subtract(request.amount()));
        Transaction transaction = new Transaction();
        transaction.setTransactionType(TransactionType.WITHDRAWAL);
        transaction.setAmount(request.amount());
        transaction.setBankAccount(account);
        transactionRepository.save(transaction);
        accountRepository.save(account);
        return new WithdrawalResponse(account.getAccount_number(),account.getName(),account.getBalance());
    }


    public List<TransactionResponse> getAllTransactions(Integer account_number){
        BankAccount account = accountRepository
                .findByUserUsernameAndAccount_number(getCurrentUsername(),account_number)
                .orElseThrow(() -> new RuntimeException("Account doesn't exist"));
        return transactionRepository.findByBankAccountOrderByCreatedAtDesc(account)
                .stream()
                .map(this::fromTransactionToResponse)
                .toList();
    }



    @Transactional
    public TransferResponse transfer(Integer account_number, TransferRequest request) {
        String currentUsername = getCurrentUsername();
        BigDecimal amount = request.amount();

        BankAccount account = accountRepository.findByUserUsernameAndAccount_number(currentUsername, account_number)
                .orElseThrow(() -> new RuntimeException("Account doesn't exist"));

        BankAccount toAccount = accountRepository.findByUserUsernameAndAccount_number(request.toUsername(), request.toAccountNumber())
                .orElseThrow(() -> new RuntimeException("Destination account doesn't exist"));

        if(currentUsername.equals(request.toUsername()) &&
        account_number.equals(request.toAccountNumber()))
        {
            throw new RuntimeException("Cannot transfer to the same account");
        }

        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        account.setBalance(account.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        Transaction transaction = new Transaction();
        transaction.setBankAccount(account);
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.TRANSFER_OUT);

        Transaction toTransaction = new Transaction();
        toTransaction.setBankAccount(toAccount);
        toTransaction.setAmount(amount);
        toTransaction.setTransactionType(TransactionType.TRANSFER_IN);

        accountRepository.save(account);
        accountRepository.save(toAccount);

        transactionRepository.save(transaction);
        transactionRepository.save(toTransaction);

        return new TransferResponse(
                currentUsername,
                account.getAccount_number(),
                account.getName(),
                account.getBalance(),

                request.toUsername(),
                toAccount.getAccount_number(),
                toAccount.getName(),
                toAccount.getBalance(),

                amount,
                "Transfer completed successfully"
        );
    }



    private String getCurrentUsername(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private TransactionResponse fromTransactionToResponse(Transaction transaction){
        BankAccount account = transaction.getBankAccount();

        return new TransactionResponse(
                transaction.getId(),
                account.getAccount_number(),
                account.getName(),
                transaction.getTransactionType(),
                transaction.getAmount(),
                transaction.getCreatedAt()
        );
    }

}
