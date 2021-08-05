package com.simple.bz.model;


import com.simple.common.error.ServiceException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="tbl_role")
public class RoleModel implements Serializable {
    public static final Long DEFAULT_ROLE_ID = 0L;
    public static final Long ADMIN_ROLE_ID = -1L;
    public static final String DEFAULT_ROLE_NAME = "guest";
    public static final String ADMIN_ROLE_NAME = "admin";
    public static final String ALL_ROLE_NAME = "all";
    public static boolean adminRole(String roleName){
        return (roleName.equalsIgnoreCase(RoleModel.ADMIN_ROLE_NAME));
    }
    public static boolean defaultRole(String roleName){
        return (roleName.equalsIgnoreCase(RoleModel.DEFAULT_ROLE_NAME));
    }
    public static boolean blackRole(String roleName){
        return (roleName.equalsIgnoreCase(RoleModel.ALL_ROLE_NAME));
    }
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long                id;
    private String              name;
    private String              description;
    private String              domain;

}
