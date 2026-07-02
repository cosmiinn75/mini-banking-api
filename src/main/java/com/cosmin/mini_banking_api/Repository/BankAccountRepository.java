package com.cosmin.mini_banking_api.Repository;

import com.cosmin.mini_banking_api.Model.BankAccount;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount,Long> {
    Optional<BankAccount> findByUserUsernameAndAccountNumber(String currentUsername, Integer accountNumber);

    Optional<BankAccount> findByUserUsernameAndName(String currentUsername, String toAccount);
    boolean existsByUserUsernameAndName(String username , String name);



    @Query("""
        SELECT b
        FROM BankAccount b
        WHERE b.user.username = :username  
        AND(:minBalance IS NULL OR b.balance >= :minBalance)
        ORDER BY b.accountNumber ASC
""")
    Page<BankAccount> findFilteredAccount(
            Pageable pageable,
            @Param("username") String username,
            @Param("minBalance")BigDecimal minBalance
            );


}
