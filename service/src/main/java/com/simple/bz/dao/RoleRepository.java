package com.simple.bz.dao;

import com.simple.bz.model.RoleModel;
import com.simple.bz.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RoleRepository extends JpaRepository<RoleModel, Long>{
    public RoleModel findOneByName(String name);

    @Modifying
    @Transactional
    @Query("delete from RoleModel s where s.id in (?1)")
    public int deleteBatch(List<Long> ids);
}
