package com.cosmin.mini_banking_api.Repository;

import com.cosmin.mini_banking_api.Enum.TransactionType;
import com.cosmin.mini_banking_api.Model.BankAccount;
import com.cosmin.mini_banking_api.Model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    List<Transaction> findByBankAccountOrderByCreatedAtDesc(BankAccount account);

    @Query("""
    SELECT t 
    FROM Transaction t
    WHERE t.bankAccount = :account
    AND (:type IS NULL OR t.transactionType = :type)
    AND(:minAmount IS NULL OR t.amount >= :minAmount)
    ORDER BY t.createdAt DESC
""")
    List<Transaction> findFilteredTransactions(
            @Param("account") BankAccount account,
            @Param("type") TransactionType type,
            @Param("minAmount")BigDecimal minAmount
            );
}
