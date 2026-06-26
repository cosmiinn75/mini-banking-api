package com.cosmin.mini_banking_api.Service;

import com.cosmin.mini_banking_api.Dto.ChangeRoleRequest;
import com.cosmin.mini_banking_api.Dto.UserResponse;
import com.cosmin.mini_banking_api.Enum.Role;
import com.cosmin.mini_banking_api.Exception.NotAdminException;
import com.cosmin.mini_banking_api.Exception.CantChangeOwnRoleException;
import com.cosmin.mini_banking_api.Exception.NotAdminException;
import com.cosmin.mini_banking_api.Model.User;
import com.cosmin.mini_banking_api.Repository.BankAccountRepository;
import com.cosmin.mini_banking_api.Repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BankAccountRepository accountRepository;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AdminService adminService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void changeRole_shouldChangeUserRole_whenCurrentUserIsAdmin() {
        mockAuthenticatedUser("admin");

        User adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("admin");
        adminUser.setRole(Role.ADMIN);

        User targetUser = new User();
        targetUser.setId(2L);
        targetUser.setUsername("cosmin");
        targetUser.setRole(Role.CUSTOMER);

        ChangeRoleRequest request = new ChangeRoleRequest(Role.ADMIN);

        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(adminUser));

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(targetUser));

        UserResponse response = adminService.changeRole(2L, request);

        assertEquals(2L, response.id());
        assertEquals("cosmin", response.username());
        assertEquals(Role.ADMIN, response.role());

        assertEquals(Role.ADMIN, targetUser.getRole());

        verify(userRepository).findByUsername("admin");
        verify(userRepository).findById(2L);
        verify(userRepository).save(targetUser);
    }

    @Test
    void changeRole_shouldThrowException_whenAdminChangesOwnRole() {
        mockAuthenticatedUser("admin");

        User adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("admin");
        adminUser.setRole(Role.ADMIN);

        ChangeRoleRequest request = new ChangeRoleRequest(Role.CUSTOMER);

        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(adminUser));

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(adminUser));

        assertThrows(
                CantChangeOwnRoleException.class,
                () -> adminService.changeRole(1L, request)
        );

        verify(userRepository).findByUsername("admin");
        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changeRole_shouldThrowException_whenCurrentUserIsNotAdmin() {
        mockAuthenticatedUser("cosmin");

        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("cosmin");
        currentUser.setRole(Role.CUSTOMER);

        ChangeRoleRequest request = new ChangeRoleRequest(Role.ADMIN);

        when(userRepository.findByUsername("cosmin"))
                .thenReturn(Optional.of(currentUser));

        assertThrows(
                NotAdminException.class,
                () -> adminService.changeRole(2L, request)
        );

        verify(userRepository).findByUsername("cosmin");
        verify(userRepository, never()).findById(anyLong());
        verify(userRepository, never()).save(any(User.class));
    }

    private void mockAuthenticatedUser(String username) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        Collections.emptyList()
                );

        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}