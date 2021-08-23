package com.simple.bz.dao;

import com.simple.bz.model.MenuModel;
import com.simple.bz.model.PermissionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MenuRepository extends JpaRepository<MenuModel, Long>{

    @Modifying
    @Transactional
    @Query("delete from MenuModel s where s.id in (?1)")
    public int deleteBatch(List<Long> ids);
}
