package com.simple.bz.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Order(value =1)
@RequiredArgsConstructor
public class RolePermissionLoader implements ApplicationRunner {
    private final RoleService roleService;

    @Override
    public void run(ApplicationArguments args){
        roleService.loadRoleAndPermissions();
    }

}