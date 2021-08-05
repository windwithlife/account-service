package com.simple.bz.service;

import com.simple.bz.dao.*;
import com.simple.bz.dto.*;
import com.simple.bz.model.*;
import com.simple.common.auth.Sessions;
import com.simple.common.error.ServiceException;
import com.simple.common.wechat.WechatHelper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class RoleService {
    private final ModelMapper modelMapper;
    private final UserRepository userDao;
    private final RoleRepository dao;
    private final ContextQuery contextQuery;
    private final PermissionRepository permissionDao;
    private final RolePermissionRepository rolePermissionDao;


    public void loadRoleAndPermissions() {
        try {

            //保证库里有可用的缺省用户及超级用户
            RoleModel roleModel = dao.findById(RoleModel.ADMIN_ROLE_ID).orElse(null);
            if (null == roleModel) {
                contextQuery.executeQuery("insert  into tbl_role (id, name,domain,description) values(-1,'admin','test','administrator')");
            }
            roleModel = dao.findById(RoleModel.DEFAULT_ROLE_ID).orElse(null);
            if (null == roleModel) {
                contextQuery.executeQuery("insert  into tbl_role (id, name,domain,description) values(0,'guest','test','default')");
            }
            roleModel = dao.findOneByName(RoleModel.ALL_ROLE_NAME);
            RoleModel allModel = null;
            if (null == roleModel) {
                allModel = dao.save(RoleModel.builder().name(RoleModel.ALL_ROLE_NAME).domain("test").description("for all role set controlled permission").build());
            }
            List<RoleModel> models = dao.findAll();
            models.forEach(roleItem -> {
                System.out.println("ROle info ====>" + roleItem.toString());
                if (RoleModel.defaultRole(roleItem.getName())) {
                    return;
                }

                if (RoleModel.adminRole(roleItem.getName())) {
                    //把Admin用户角色设入角色白名单中
                    Sessions.setRoleWhiteList(roleItem.getName());
                }

                List<RolePermissionModel> controlledPermissions = rolePermissionDao.findByRoleId(roleItem.getId());
                controlledPermissions.forEach(permissionItem -> {
                    PermissionModel permission = permissionDao.findById(permissionItem.getPermissionId()).orElse(null);
                    if (null != permission) {
                        if (RoleModel.blackRole(roleItem.getName())) {
                            Sessions.setForbidPermission(permission.getUri(), permission.getMethod());
                        } else {
                            Sessions.setRolePermission(roleItem.getName(), permission.getUri(), permission.getMethod(), roleItem.getDescription());
                        }
                    }
                });


            });

            System.out.println("successful to preload the authorization permission");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RoleModel convertToModel(RoleDto dto) {
        return this.modelMapper.map(dto, RoleModel.class);
    }

    public List<RoleModel> convertToModels(List<RoleDto> dtos) {
        List<RoleModel> resultList = new ArrayList<RoleModel>();
        for (int i = 0; i < dtos.size(); i++) {
            resultList.add(this.convertToModel(dtos.get(i)));
        }
        return resultList;
    }

    public RoleDto convertToDto(RoleModel model) {
        return this.modelMapper.map(model, RoleDto.class);
    }

    public List<RoleDto> convertToDtos(List<RoleModel> models) {
        List<RoleDto> resultList = new ArrayList<RoleDto>();
        for (int i = 0; i < models.size(); i++) {
            resultList.add(this.convertToDto(models.get(i)));
        }
        return resultList;

    }

    public List<RoleDto> findAll() {

        List<RoleModel> list = dao.findAll();
        return this.convertToDtos(list);
    }


    public RoleDto findById(Long id) {
        RoleModel model = dao.findById(id).get();
        return this.convertToDto(model);
    }


    public List<RoleDto> queryAll() {
        List<RoleDto> list = contextQuery.findList("select * from tbl_role", RoleDto.class);
        return list;
    }


    public RoleDto save(RoleDto item) {
        RoleModel model = this.convertToModel(item);
        RoleModel newRole = this.dao.save(model);
        return this.convertToDto(newRole);
    }

    public PermissionBindDto bindPermission(PermissionBindDto item) {
        Long roleId = item.getRoleId();
        RoleModel roleModel = null;
        if (null == roleId || roleId < 0) {
            roleModel = RoleModel.builder().id(0L).name("guest").build();
        } else {
            roleModel = dao.findById(item.getRoleId()).orElse(null);
        }

        PermissionModel model = this.permissionDao.findById(item.getPermissionId()).orElse(null);
        if (null == model) {
            return null;
        }
        RolePermissionModel bindPermission = rolePermissionDao.save(RolePermissionModel.builder().roleId(item.getRoleId()).permissionId(item.getPermissionId()).build());
        Sessions.setRolePermission(roleModel.getName(), model.getUri(), model.getMethod(), roleModel.getName());
        item.setId(bindPermission.getId());
        return item;
    }

    public PermissionBindDto bindControlledPermission(PermissionBindDto item) {

        RoleModel roleModel = dao.findOneByName(RoleModel.ALL_ROLE_NAME);
        PermissionModel model = this.permissionDao.findById(item.getPermissionId()).orElse(null);
        if (null == model) {
            return null;
        }
        RolePermissionModel bindPermission = rolePermissionDao.save(RolePermissionModel.builder().roleId(roleModel.getId()).permissionId(item.getPermissionId()).build());
        Sessions.setForbidPermission(model.getUri(), model.getMethod());
        item.setId(bindPermission.getId());
        return item;
    }

    public RoleDto update(RoleDto item) {
        Long id = item.getId();
        RoleModel model = dao.findById(id).get();
        this.modelMapper.map(item, model);

        System.out.println(model.toString());
        this.dao.save(model);
        return item;
    }

    public void remove(Long id) {
        this.dao.deleteById(id);
    }

}