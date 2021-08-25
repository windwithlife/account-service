package com.simple.bz.controller;

import com.simple.bz.dto.*;
import com.simple.bz.service.AccountService;
import com.simple.bz.service.RoleService;
import com.simple.common.api.SimpleRequest;
import com.simple.common.api.SimpleResponse;
import com.simple.common.auth.AuthModel;
import com.simple.common.auth.LoginUser;
import com.simple.common.auth.SessionUser;
import com.simple.common.auth.Sessions;
import com.simple.common.controller.BaseController;
import com.simple.common.error.ServiceException;
import com.simple.common.props.AppProps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/role")
@Api(tags = "帐户角色相关的SOA集")
public class RoleController extends BaseController {

    private final RoleService service;
    private final AppProps appProps;

    @ApiOperation(value="库里所有帐户角色（用于测试)")
    @PostMapping(path = "/queryAll")
    public SimpleResponse<RolesDto> queryAll (){

        List<RoleDto> roles = service.queryAll();
        SimpleResponse<RolesDto> result = new SimpleResponse<RolesDto>();

        return result.success(RolesDto.builder().roles(roles).build());
    }

    @ApiOperation(value="根据ID获取角色信息",notes = "")
    @PostMapping(path = "/findById")
    public SimpleResponse<RoleDto> findById (@RequestBody SimpleRequest<IDRequest> request){
        Long houseId = request.getParams().getId();
        System.out.println("applicationId:" + houseId);
        RoleDto  dto = service.findById(houseId);
        SimpleResponse<RoleDto> result = new SimpleResponse<RoleDto>();
        return result.success(dto);
    }



    @ApiOperation(value="新增角色",notes = "")
    @PostMapping(path = "/addRole")
    public SimpleResponse<RoleDto> addNewRole (@RequestBody SimpleRequest<RoleDto> request){
        RoleDto dto = request.getParams();
        RoleDto data = service.save(dto);
        SimpleResponse<RoleDto> result = new SimpleResponse<RoleDto>();
        return result.success(data);

    }

    @ApiOperation(value="修改信息",notes = "")
    @PostMapping(path = "/update")
    public SimpleResponse<RoleDto> updateSave(@RequestBody SimpleRequest<RoleDto> req) {
        RoleDto dto = req.getParams();
        System.out.println(dto.toString());
        service.update(dto);
        SimpleResponse<RoleDto> result = new SimpleResponse<RoleDto>();
        return result.success(dto);

    }

    @ApiOperation(value="删除角色",notes = "")
    @ResponseBody
    @RequestMapping(value = "/removeById", method = RequestMethod.POST)
    public SimpleResponse<IDResponse> removeById(@RequestBody SimpleRequest<IDRequest> req) {
        service.remove(req.getParams().getId());
        SimpleResponse<IDResponse> result = new SimpleResponse<IDResponse>();

        return result.success(IDResponse.builder().id(req.getParams().getId()).build());
    }

    @ApiOperation(value="批量删除角色",notes = "")
    @ResponseBody
    @RequestMapping(value = "/removeBatch", method = RequestMethod.POST)
    public SimpleResponse<IDResponse> removeBatch(@RequestBody SimpleRequest<BatchIDRequest> req) {
        System.out.println(req.getParams().getIds().toString());
        SimpleResponse<IDResponse> result = new SimpleResponse<IDResponse>();
        service.removeBatch(req.getParams().getIds());
        return result.success(IDResponse.builder().id(0L).build());
    }


    @ApiOperation(value="绑定权限到角色",notes = "")
    @PostMapping(path = "/bindPermission")
    public SimpleResponse<PermissionBindDto> bindPermission (@RequestBody SimpleRequest<PermissionBindDto> request){
        PermissionBindDto dto = request.getParams();
        PermissionBindDto data = service.bindPermission(dto);
        SimpleResponse<PermissionBindDto> result = new SimpleResponse<PermissionBindDto>();
        return result.success(data);
    }

    @ApiOperation(value="新增受全局控权限条目",notes = "增加全局受控权限")
    @PostMapping(path = "/addControlledPermission")
    public SimpleResponse<PermissionBindDto> addAllPermission (@RequestBody SimpleRequest<PermissionBindDto> request){
        PermissionBindDto dto = request.getParams();
        PermissionBindDto data = service.bindControlledPermission(dto);
        SimpleResponse<PermissionBindDto> result = new SimpleResponse<PermissionBindDto>();
        return result.success(data);
    }
}
