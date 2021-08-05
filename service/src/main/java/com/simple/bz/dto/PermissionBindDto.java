package com.simple.bz.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionBindDto {
    private Long                id;
    private Long                roleId;
    private Long                permissionId;
    private String              name;
}
