package com.simple.bz.dao;

import com.simple.bz.model.AccountModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<AccountModel, String>{
    public AccountModel findOneByName(String name);
}
