package com.simple.bz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class LoginResponse {
    private String name;
    private String token;
    private String openId;
    private String photoUrl;
    private List<String> roles;
    private boolean isLogin;
}
