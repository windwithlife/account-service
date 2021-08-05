package com.simple.bz.controller;

import com.simple.bz.dto.*;
import com.simple.bz.service.PermissionService;
import com.simple.bz.service.RoleService;
import com.simple.common.api.BaseResponse;
import com.simple.common.api.GenericResponse;
import com.simple.common.api.SimpleRequest;
import com.simple.common.api.SimpleResponse;
import com.simple.common.controller.BaseController;
import com.simple.common.props.AppProps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/permission")
@Api(tags = "帐户权限相关的SOA集")
public class PermissionController extends BaseController {

    private final PermissionService service;
    private final AppProps appProps;

    @ApiOperation(value="库里所有帐户权限（用于测试)")
    @PostMapping(path = "/queryAll")
    public SimpleResponse<PermissionsDto> queryAll (){

        List<PermissionDto> permissions = service.queryAll();
        SimpleResponse<PermissionsDto> result = new SimpleResponse<PermissionsDto>();

        return result.success(PermissionsDto.builder().permissions(permissions).build());
    }

    @GetMapping(path = "/testAuthorize")
    public BaseResponse testAuth (@RequestParam("param") String param){

        return GenericResponse.buildSuccess("okok response ==>" + param);
    }
    @ApiOperation(value="根据ID获取权限信息",notes = "")
    @PostMapping(path = "/findById")
    public SimpleResponse<PermissionDto> findById (@RequestBody SimpleRequest<IDRequest> request){
        Long id = request.getParams().getId();
        System.out.println("applicationId:" + id);
        PermissionDto  dto = service.findById(id);
        SimpleResponse<PermissionDto> result = new SimpleResponse<PermissionDto>();
        return result.success(dto);
    }



    @ApiOperation(value="新增权限",notes = "")
    @PostMapping(path = "/addPermission")
    public SimpleResponse<PermissionDto> addNewPermission (@RequestBody SimpleRequest<PermissionNewDto> request){
        PermissionNewDto dto = request.getParams();
        PermissionDto data = service.save(dto);
        SimpleResponse<PermissionDto> result = new SimpleResponse<PermissionDto>();
        return result.success(data);
    }


    @ApiOperation(value="修改权限信息",notes = "")
    @PostMapping(path = "/update")
    public SimpleResponse<PermissionDto> updateSave(@RequestBody SimpleRequest<PermissionDto> req) {
        PermissionDto dto = req.getParams();
        System.out.println(dto.toString());
        service.update(dto);
        SimpleResponse<PermissionDto> result = new SimpleResponse<PermissionDto>();
        return result.success(dto);

    }

    @ApiOperation(value="删除权限",notes = "")
    @ResponseBody
    @RequestMapping(value = "/removeById", method = RequestMethod.POST)
    public SimpleResponse<IDResponse> removeById(@RequestBody SimpleRequest<IDRequest> req) {
        service.remove(req.getParams().getId());
        SimpleResponse<IDResponse> result = new SimpleResponse<IDResponse>();

        return result.success(IDResponse.builder().id(req.getParams().getId()).build());
    }
}
