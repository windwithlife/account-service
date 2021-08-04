package com.simple.bz.dao;

import com.simple.bz.model.RoleModel;
import com.simple.bz.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleModel, Long>{
}
