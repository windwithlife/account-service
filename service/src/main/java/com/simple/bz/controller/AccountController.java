package com.simple.bz.controller;

import com.simple.bz.dto.AccountDto;

import com.simple.bz.dto.UserDto;
import com.simple.bz.service.AccountService;

import com.simple.common.api.SimpleRequest;
import com.simple.common.api.SimpleResponse;
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

@AllArgsConstructor
@RestController
@RequestMapping("/account")
@Api(tags = "帐户相关的SOA集")
public class AccountController extends BaseController {

    private final AccountService service;
    private final AppProps appProps;

//    @ApiOperation(value="库里所有帐户（用于测试)"）
//    @PostMapping(path = "/queryAll")
//    public SimpleResponse<AccountDto> queryAll (){
//
//        List<AccountDto> rooms = service.queryAll();
//        SimpleResponse<RoomsDto> result = new SimpleResponse<RoomsDto>();
//
//        return result.success(RoomsDto.builder().roomList(rooms).houseId(-1L).build());
////    }
    @ApiOperation(value="微信登录及验证")
    @PostMapping(path = "/wechatLogin")
    public SimpleResponse<String>  wechatLogin (@RequestBody SimpleRequest<String> request, HttpServletResponse response){
        String code = request.getParams();
        String token = service.wechatLogin(code);
        String domainName = appProps.getDomainName();
        Sessions.writeToken(token,domainName,true,response);
        SimpleResponse<String> result = new SimpleResponse<String>();
        return  result.success(token);


    }
    @ApiOperation(value="用记注册")
    @PostMapping(path = "/signup")
    public SimpleResponse<AccountDto>  signup (@RequestBody SimpleRequest<AccountDto> request, HttpServletResponse response){
        AccountDto dto = request.getParams();
        AccountDto result = this.service.signup(dto);
        if (null == result){
            throw new ServiceException("注册失败");
        }
//        String token = service.login(dto);
//        if (StringUtils.isBlank(token)){
//            throw new ServiceException("注册失败");
//        }
//        dto.setToken(token);
//        String domainName = appProps.getDomainName();
//        Sessions.writeToken(token,domainName,true,response);

        System.out.println("sign account dto info ===>" +  result.toString());
        SimpleResponse<AccountDto> ret= new SimpleResponse<AccountDto>();
        return  ret.success(result);

    }
    @ApiOperation(value="普通用户登录")
    @PostMapping(path = "/login")
    public SimpleResponse<AccountDto>  login (@RequestBody SimpleRequest<AccountDto> request, HttpServletResponse response){
        AccountDto account = request.getParams();

        String token = service.login(account);
        if (StringUtils.isBlank(token)){
            throw new ServiceException("登录失败");
        }
        account.setToken(token);
        String domainName = appProps.getDomainName();
        Sessions.writeToken(token,domainName,true,response);
        SimpleResponse<AccountDto> ret= new SimpleResponse<AccountDto>();
        return  ret.success(account);

    }
    @ApiOperation(value="普通用户登出")
    @PostMapping(path = "/logout")
    public SimpleResponse<AccountDto>  logout (@RequestBody SimpleRequest<AccountDto> params,
                                               HttpServletRequest request,
                                               HttpServletResponse response){
        AccountDto account = params.getParams();
        try{
            String domain = appProps.getDomainName();
            Sessions.logout(domain, request, response);
        }catch (Exception e){

        }
        SimpleResponse<AccountDto> ret= new SimpleResponse<AccountDto>();
        return  ret.success(account);

    }

    @ApiOperation(value="修改基本用户信息")
    @PostMapping(path = "/updateAccount")
    public SimpleResponse<AccountDto> updateAccount (@RequestBody SimpleRequest<AccountDto> params){
        AccountDto account = params.getParams();
        service.updateAccount(account);
        SimpleResponse<AccountDto> ret= new SimpleResponse<AccountDto>();
        return  ret.success(account);

    }


    @ApiOperation(value="修改基本用户扩展信息")
    @PostMapping(path = "/updateUserInfo")
    public SimpleResponse<UserDto> updateUserInfo (@RequestBody SimpleRequest<UserDto> params){
        UserDto userInfo = params.getParams();
        service.updateUserInfo(userInfo);
        SimpleResponse<UserDto> ret= new SimpleResponse<UserDto>();
        return  ret.success(userInfo);
    }

    @ApiOperation(value="获取当前登录用户信息")
    @PostMapping(path = "/getCurrentUserInfo")
    public SimpleResponse<UserDto> getCurrentUserInfo (HttpServletRequest request){
        String token =  Sessions.getAuthToken(request);
        if (StringUtils.isNotBlank(token)){
            String userId = Sessions.getSessionUserInfo(token).getUserId();
            UserDto userInfo = service.getUserInfoByUserId(userId);
            SimpleResponse<UserDto> ret= new SimpleResponse<UserDto>();
            return  ret.success(userInfo);
        }else{
            SimpleResponse<UserDto> ret= new SimpleResponse<UserDto>();
            return ret.failure("当前未登录");
        }

    }

}
