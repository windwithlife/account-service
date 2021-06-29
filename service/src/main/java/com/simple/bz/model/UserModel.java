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

/**
 * 用户实体
 * @author hejinguo
 * @version $Id: UsersModel.java, v 0.1 2020年7月13日 下午3:15:20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="tbl_user")
public class UserModel implements Serializable {

    private static final long   serialVersionUID    = -3299069399674355478L;

    /**用户状态(0:可用)*/
    public static final Byte    USER_STATUS_USABLE  = 0;
    /**用户状态(1:禁用)*/
    public static final Byte    USER_STATUS_DISABLE = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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


    public static void validateUserStatus(UserModel userModel) {
        if (userModel == null) {
            throw new ServiceException("用户信息不存在!");
        }
        if (userModel.getUserStatus() != 0) {
            throw new ServiceException("当前用户非正常状态!");
        }
    }

    /**
     * 用户注册参数验证
     * @throws Exception
     */
    public static void validateRegistUserParam(String userNickName, String headPic,
                                               Byte userGrenderWx, String encryptedData, String iv,
                                               String openId) throws Exception {
        if (StringUtils.isBlank(userNickName)) {
            throw new ServiceException("用户昵称不能为空!");
        }
        if (StringUtils.isBlank(headPic)) {
            throw new ServiceException("用户头像不能为空!");
        }
        if (userGrenderWx == null) {
            throw new ServiceException("用户性别不能为空!");
        }
        if (StringUtils.isBlank(encryptedData)) {
            //logger.error("用户注册时获取手机号授权登录信息为空,参数信息：encryptedData--->{}", encryptedData);
            throw new ServiceException("微信信息获取失败,请重新授权登录!");
        }
        if (StringUtils.isBlank(iv)) {
            //logger.error("用户注册时获取手机号授权登录信息为空,参数信息：iv--->{}", iv);
            throw new ServiceException("微信信息获取失败,请重新授权登录!");
        }
        if (StringUtils.isBlank(openId)) {
            //logger.error("用户注册时获取手机号授权登录信息为空,参数信息：openId--->{}", openId);
            throw new ServiceException("微信信息获取失败,请重新授权登录!");
        }
    }



    /**
     * pc用户登录参数验证
     * @param userName
     * @param passWord
     * @throws Exception
     */
    public static void validateUserLoginParam(String userName, String passWord) throws Exception {
        if (StringUtils.isBlank(userName)) {
            throw new ServiceException("登录用户名不能为空!");
        }
        if (StringUtils.isBlank(passWord)) {
            throw new ServiceException("登录密码不能为空!");
        }
    }

    /**
     * 微信公众号授权登录或注册参数验证
     */
    public static void validateRegisterWechatPublicUserParam(String code) throws Exception {
        if (StringUtils.isBlank(code)) {
            //logger.error("微信公众号授权登录或注册时code不能为空,参数信息：code--->{}",code);
            throw new ServiceException("请求参数错误,请重新授权登录!");
        }
    }



}
