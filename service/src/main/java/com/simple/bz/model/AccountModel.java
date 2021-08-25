package com.simple.bz.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
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

    public static final String ADMIN_USER_NAME = "admin";

    public static boolean isAdministrator(String name){
        return (AccountModel.ADMIN_USER_NAME.equalsIgnoreCase(name));
    }

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
    private String unionId;
    private String  email;
    private int     type;
    private String  createdName; //创建人
    private boolean confirmedAndActive;
    private String phoneNumber;
    private String photoUrl;
}
