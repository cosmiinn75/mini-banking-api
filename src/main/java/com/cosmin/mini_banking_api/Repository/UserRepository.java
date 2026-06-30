package com.cosmin.mini_banking_api.Repository;

import com.cosmin.mini_banking_api.Dto.StatsResponse;
import com.cosmin.mini_banking_api.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);

    @Query(
            nativeQuery = true,
            value = """
                SELECT 
                    u.email AS email,
                    COALESCE(a.number_of_accounts, 0) AS numberOfAccounts,
                    COALESCE(a.total_balance, 0) AS totalBalance,
                    COALESCE(t.number_of_transactions, 0) AS numberOfTransactions
                FROM users u
                LEFT JOIN (
                    SELECT 
                        b.user_id AS user_id,
                        COUNT(b.id) AS number_of_accounts,
                        SUM(b.balance) AS total_balance
                    FROM bank_accounts b
                    GROUP BY b.user_id
                ) a ON a.user_id = u.id
                LEFT JOIN (
                    SELECT 
                        b.user_id AS user_id,
                        COUNT(t.id) AS number_of_transactions
                    FROM bank_accounts b
                    JOIN transactions t ON t.bank_account_id = b.id
                    GROUP BY b.user_id
                ) t ON t.user_id = u.id
                """
    )
    List<StatsResponse> getUsersStats();
    boolean existsByEmail(String email);
}
