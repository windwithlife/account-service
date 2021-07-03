package com.simple.bz.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="tbl_account")
public class AccountModel {
    @Id
    @GenericGenerator(name = "user-uuid", strategy = "uuid")
    @GeneratedValue(generator = "user-uuid")
    @Column(columnDefinition = "varchar(250)")
    private String id;
    private String name;
    private String nickName;
    private String password;
    private String passwordHash;
    private String openId;
    private String  email;
    private AccountType type;
    private boolean confirmedAndActive;
    private String phoneNumber;
    private String photoUrl;
}
