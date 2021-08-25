package com.simple.bz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class LoginRequest {
    private String username;
    private String password;
    private String email;
    private String mobile;
    private String domain;
    private boolean autoLogin;
    private String type;
}
