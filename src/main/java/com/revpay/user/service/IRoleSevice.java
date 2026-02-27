package com.revpay.user.service;

import com.revpay.domain.entity.user.Role;

public interface IRoleSevice {
    Role getDefaultRole();
    Role saveRole(Role role);
}
