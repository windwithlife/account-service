package com.simple.common.wechat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WechatUserInfo {
    private String id;
    private String nickName;
    private Integer sex;
    private String openId;
    private String unionId;
    private String headImgUrl;
    private String country;
    private String province;
    private String city;
    private String phoneNumber;

}
