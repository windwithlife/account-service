package com.simple.bz.dto;


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
public class UserDto implements Serializable {
    private Long                id;
    private String              userId;
    /**用户昵称*/
    private String              userNickName;
    /**用户真实姓名*/
    private String              userTrueName;
    /**登录名*/
    private String              loginName;
    /**用户密码*/
    private String              passWord;
    /**用户手机号*/
    private String              userMobile;
    /**用户头像*/
    private String              headPic;
    /**小程序openID*/
    private String              wechatOpenId;
    /**公众号openId*/
    private String              wechatPublicOpenId;
    /**微信unionid*/
    private String              unionId;
    /**用户token*/
    private String              userToken;
    /**用户类型(0:用户 1:讲师 2:管理员)*/
    private Integer             userType;
    /**用户状态(0:可用  1:禁用)*/
    private Byte                userStatus;
    /**版本号*/
    private Integer             version;
    /**创建人*/
    private String              createdName;
    /**创建时间*/
    private Date                createdDate;
    /**修改人*/
    private String              updatedName;
    /**修改时间*/
    private Date                updatedDate;

}
