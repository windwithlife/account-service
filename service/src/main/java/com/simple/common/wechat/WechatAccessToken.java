package com.simple.common.wechat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WechatAccessToken {

    private String  accessToken;
    private Integer expiresIn ;
    private String  refresh_token;
    private String  openId;
    private String  scope;

}
