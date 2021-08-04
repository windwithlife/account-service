package com.simple.bz.dao;

import com.simple.bz.model.RoleModel;
import com.simple.bz.model.RolePermissionModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolePermissionRepository extends JpaRepository<RolePermissionModel, Long>{
}
