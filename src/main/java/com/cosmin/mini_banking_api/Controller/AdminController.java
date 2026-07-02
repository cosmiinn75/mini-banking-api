package com.cosmin.mini_banking_api.Controller;

import com.cosmin.mini_banking_api.Dto.AccountResponse;
import com.cosmin.mini_banking_api.Dto.ChangeRoleRequest;
import com.cosmin.mini_banking_api.Dto.StatsResponse;
import com.cosmin.mini_banking_api.Dto.UserResponse;
import com.cosmin.mini_banking_api.Service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;




@Tag(
        name = "Admin",
        description = "Admin-only endpoints for managing users, accounts, roles and user statistics"
)
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }


    @Operation(
            summary = "Get all users",
            description = "Returns a list of all registered users. This endpoint is accessible only to users with ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users returned successfully"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "User does not have ADMIN role")
    })
    @GetMapping("/users")
    public List<UserResponse> getAllUsers(){
        return adminService.getAllUsers();
    }


    @Operation(
            summary = "Get all bank accounts",
            description = "Returns all bank accounts from the system, across all users. This endpoint is accessible only to users with ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accounts returned successfully"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "User does not have ADMIN role")
    })
    @GetMapping("/accounts")
    public List<AccountResponse> getAllAccounts(){
        return adminService.getAllAccounts();
    }



    @Operation(
            summary = "Get user statistics",
            description = "Returns aggregated statistics for each user, including number of accounts, total balance and number of transactions. This endpoint is accessible only to users with ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User statistics returned successfully"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "User does not have ADMIN role")
    })
    @GetMapping("/users/stats")
    public List<StatsResponse> getUsersStats(){
        return adminService.getUsersStats();
    }


    @Operation(
            summary = "Change user role",
            description = "Changes the role of a specific user. This endpoint is accessible only to users with ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User role changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid role request"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "User does not have ADMIN role or tries to change their own role"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/users/{id}/role")
    public UserResponse changeRole(@PathVariable Long id, @Valid @RequestBody ChangeRoleRequest request){
        return adminService.changeRole(id,request);
    }

}
