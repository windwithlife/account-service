package com.simple.bz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class AccountLoginDto {
    private String username;
    private String loginName;
    private String password;
    private String token;
    private String email;
    private String mobile;
    private String domain;
    private String photoUrl;
    private boolean autoLogin;
    private String type;
}
