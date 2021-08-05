package com.simple.bz.dao;

import com.simple.bz.model.AccountRoleModel;
import com.simple.bz.model.RoleModel;
import com.simple.bz.model.RolePermissionModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRoleRepository extends JpaRepository<AccountRoleModel, Long>{
}
