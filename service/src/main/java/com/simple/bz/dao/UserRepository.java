package com.simple.bz.dao;

import com.simple.bz.model.AccountModel;
import com.simple.bz.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserModel, Long>{
    public UserModel findByUserId(String userId);
}
