package com.simple.bz.dao;

import com.simple.bz.model.PermissionModel;
import com.simple.bz.model.RoleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PermissionRepository extends JpaRepository<PermissionModel, Long>{

    @Modifying
    @Transactional
    @Query("delete from PermissionModel s where s.id in (?1)")
    public int deleteBatch(List<Long> ids);
}
