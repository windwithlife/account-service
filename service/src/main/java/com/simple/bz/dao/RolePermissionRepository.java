package com.simple.bz.dao;


import com.simple.bz.model.RolePermissionModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RolePermissionRepository extends JpaRepository<RolePermissionModel, Long>{
    public List<RolePermissionModel> findByRoleId(Long roleId);
}
