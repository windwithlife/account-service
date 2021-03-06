package com.simple.bz.dto;

import com.simple.bz.model.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class AccountDto {
    private String id;
    private String name;
    private String nickName;
    private String loginName;
    private String password;
    private String token;
    private String email;
    private String phoneNumber;
    private String photoUrl;
    private String roles;
    private int    type;
    public String createUserId(){
        return "";
    }
}
