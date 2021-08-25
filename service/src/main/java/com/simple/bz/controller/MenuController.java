package com.simple.bz.controller;

import com.simple.bz.dto.*;
import com.simple.bz.service.MenuService;
import com.simple.bz.service.PermissionService;
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
@RequestMapping("/menu")
@Api(tags = "帐户权限相关的SOA集")
public class MenuController extends BaseController {

    private final MenuService service;
    private final AppProps appProps;

    @ApiOperation(value="库里所有菜单（用于测试)")
    @PostMapping(path = "/queryAll")
    public SimpleResponse<MenusDto> queryAll (){

        List<MenuDto> menus = service.queryAll();
        SimpleResponse<MenusDto> result = new SimpleResponse<MenusDto>();

        return result.success(MenusDto.builder().menus(menus).build());
    }

    @GetMapping(path = "/test")
    public BaseResponse test (@RequestParam("param") String param){

        return GenericResponse.buildSuccess("okok response ==>" + param);
    }
    @ApiOperation(value="根据ID获取权限信息",notes = "")
    @PostMapping(path = "/findById")
    public SimpleResponse<MenuDto> findById (@RequestBody SimpleRequest<IDRequest> request){
        Long id = request.getParams().getId();
        System.out.println("id:" + id);
        MenuDto  dto = service.findById(id);
        SimpleResponse<MenuDto> result = new SimpleResponse<MenuDto>();
        return result.success(dto);
    }



    @ApiOperation(value="新增权限",notes = "")
    @PostMapping(path = "/addMenu")
    public SimpleResponse<MenuDto> addNewMenu (@RequestBody SimpleRequest<MenuNewDto> request){
        MenuNewDto dto = request.getParams();
        MenuDto data = service.save(dto);
        SimpleResponse<MenuDto> result = new SimpleResponse<MenuDto>();
        return result.success(data);
    }


    @ApiOperation(value="修改权限信息",notes = "")
    @PostMapping(path = "/update")
    public SimpleResponse<MenuDto> updateSave(@RequestBody SimpleRequest<MenuDto> req) {
        MenuDto dto = req.getParams();
        System.out.println(dto.toString());
        service.update(dto);
        SimpleResponse<MenuDto> result = new SimpleResponse<MenuDto>();
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
    @ApiOperation(value="批量删除角色",notes = "")
    @ResponseBody
    @RequestMapping(value = "/removeBatch", method = RequestMethod.POST)
    public SimpleResponse<IDResponse> removeBatch(@RequestBody SimpleRequest<BatchIDRequest> req) {
        System.out.println(req.getParams().getIds().toString());
        SimpleResponse<IDResponse> result = new SimpleResponse<IDResponse>();
        service.removeBatch(req.getParams().getIds());
        return result.success(IDResponse.builder().id(0L).build());
    }

}
