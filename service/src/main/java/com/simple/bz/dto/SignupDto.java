package com.simple.bz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class SignupDto {
    private String username;
    private String nickName;
    private String email;
    private String phoneNumber;
    private String password;
    private int    type;
}
