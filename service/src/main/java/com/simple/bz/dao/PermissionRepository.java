package com.simple.bz.dao;

import com.simple.bz.model.PermissionModel;
import com.simple.bz.model.RoleModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<PermissionModel, Long>{

}
