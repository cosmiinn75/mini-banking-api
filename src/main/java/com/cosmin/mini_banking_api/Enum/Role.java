package com.cosmin.mini_banking_api.Enum;

import java.util.List;

public enum Role {
    CUSTOMER(List.of(
            Permissions.MAKE_ACCOUNTS,
            Permissions.READ_ACCOUNTS,
            Permissions.MAKE_TRANSACTIONS,
            Permissions.READ_TRANSACTIONS
    )),
    ADMIN(List.of(
            Permissions.MAKE_ACCOUNTS,
            Permissions.READ_ACCOUNTS,
            Permissions.MAKE_TRANSACTIONS,
            Permissions.READ_TRANSACTIONS,
            Permissions.READ_ALL_USERS_ACCOUNTS,
            Permissions.UPDATE_ROLE
    ));

    private final List<Permissions> permissionsList;

    Role(List<Permissions> permissionsList) {
        this.permissionsList = permissionsList;
    }

    public List<Permissions> getPermissions() {
        return permissionsList;
    }
}
