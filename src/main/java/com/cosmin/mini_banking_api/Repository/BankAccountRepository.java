package com.cosmin.mini_banking_api.Repository;

import com.cosmin.mini_banking_api.Dto.AccountResponse;
import com.cosmin.mini_banking_api.Model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount,Long> {
    Optional<BankAccount> findByUserUsernameAndAccount_number(String currentUsername, Integer accountNumber);

    Optional<BankAccount> findByUserUsernameAndName(String currentUsername, String toAccount);
    boolean existsByUserUsernameAndName(String username , String name);

    List<BankAccount> getAllByUserUsername(String userUsername);
}
