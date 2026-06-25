package com.cosmin.mini_banking_api.Controller;

import com.cosmin.mini_banking_api.Dto.AccountResponse;
import com.cosmin.mini_banking_api.Dto.ChangeRoleRequest;
import com.cosmin.mini_banking_api.Dto.UserResponse;
import com.cosmin.mini_banking_api.Service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public List<UserResponse> getAllUsers(){
        return adminService.getAllUsers();
    }

    @GetMapping("/accounts")
    public List<AccountResponse> getAllAccounts(){
        return adminService.getAllAccounts();
    }

    @PutMapping("/users/{id}/role")
    public UserResponse changeRole(@PathVariable Long id, @Valid @RequestBody ChangeRoleRequest request){
        return adminService.changeRole(id,request);
    }

}
