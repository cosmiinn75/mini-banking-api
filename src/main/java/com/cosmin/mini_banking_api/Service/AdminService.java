package com.cosmin.mini_banking_api.Service;

import com.cosmin.mini_banking_api.Dto.AccountResponse;
import com.cosmin.mini_banking_api.Dto.ChangeRoleRequest;
import com.cosmin.mini_banking_api.Dto.UserResponse;
import com.cosmin.mini_banking_api.Enum.Role;
import com.cosmin.mini_banking_api.Exception.NotAdminException;
import com.cosmin.mini_banking_api.Exception.CantChangeOwnRoleException;
import com.cosmin.mini_banking_api.Exception.UserNotFoundException;
import com.cosmin.mini_banking_api.Repository.BankAccountRepository;
import com.cosmin.mini_banking_api.Repository.UserRepository;
import com.cosmin.mini_banking_api.Model.User;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final BankAccountRepository accountRepository;
    private final AccountService accountService;

    public AdminService(UserRepository userRepository, BankAccountRepository accountRepository, AccountService accountService) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.accountService = accountService;
    }
    public List<UserResponse> getAllUsers(){
        authUser();
        return userRepository.findAll()
                .stream()
                .map(this::fromUserToResponse)
                .toList();
    }

    public List<AccountResponse> getAllAccounts(){
        authUser();
        return accountRepository.findAll().stream()
                .map(accountService::fromAccountToResponse)
                .toList();
    }

    @Transactional
    public UserResponse changeRole(Long id,ChangeRoleRequest request){
        User currentUser = getCurrentUser();
        if(currentUser.getRole() != Role.ADMIN){
            throw new NotAdminException("Access denied");
        }

        User changedUser = userRepository.findById(id)
                .orElseThrow(()-> new UserNotFoundException("User not found"));
        if(changedUser.getId().equals(currentUser.getId())){
            throw new CantChangeOwnRoleException("You can't change your own role");
        }
        changedUser.setRole(request.role());
        userRepository.save(changedUser);
        return new UserResponse(changedUser.getId(), changedUser.getUsername(), changedUser.getRole());
    }


    private void authUser(){
        User currentUser = getCurrentUser();

        if(currentUser.getRole() != Role.ADMIN){
            throw new NotAdminException("Access denied");
        }
    }

    private User getCurrentUser(){
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        return userRepository.findByUsername(username).orElseThrow(()-> new RuntimeException("Invalid user"));

    }

    private UserResponse fromUserToResponse(User user){
        return new UserResponse(user.getId(),user.getUsername(),user.getRole());
    }
}
