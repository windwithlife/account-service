package com.simple.bz.dao;

import com.simple.bz.model.AccountModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<AccountModel, String>{
    public AccountModel findOneByName(String name);
    public AccountModel findOneByOpenId(String openId);
    public List<AccountModel> findByOpenId(String openId);

}
