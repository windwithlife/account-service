package com.simple.bz.dao;

import com.simple.bz.model.AccountModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AccountRepository extends JpaRepository<AccountModel, String>{
    public AccountModel findOneByName(String name);
    public AccountModel findOneByOpenId(String openId);
    public List<AccountModel> findByOpenId(String openId);
    public List<AccountModel> findByLoginName(String name);

    @Modifying
    @Transactional
    @Query("delete from AccountModel s where s.id in (?1)")
    public int deleteBatch(List<String> ids);

}
