package com.simple.bz.controller;

import com.simple.bz.dto.*;

import com.simple.bz.model.AccountType;
import com.simple.bz.service.AccountService;

import com.simple.common.api.*;
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
@RequestMapping("/account")
@Api(tags = "帐户相关的SOA集")
public class AccountController extends BaseController {

    private final AccountService service;
    private final AppProps appProps;

    @ApiOperation(value="获取平台系统管理用户")
    @PostMapping(path = "/queryAdminUsers")
    public SimpleResponse<AccountsDto> queryAdminUsers(){
        List<AccountDto> accounts = service.queryAdminUsers();
        SimpleResponse<AccountsDto> result = new SimpleResponse<AccountsDto>();
        return result.success(AccountsDto.builder().accounts(accounts).build());
    }


    @ApiOperation(value="新增系统管理用户")
    @PostMapping(path = "/addAdminUser")
    public SimpleResponse<AccountDto> addAdminUser (@RequestBody SimpleRequest<AccountDto> params){
        AccountDto account = params.getParams();
        AccountDto newAccount = service.addAdminUser(account);
        SimpleResponse<AccountDto> ret= new SimpleResponse<AccountDto>();
        return  ret.success(newAccount);

    }

    @ApiOperation(value="删除系统管理帐户")
    @PostMapping(path = "/removeAdminUser")
    public SimpleResponse<IDStringRequest> removeAdminUser (@RequestBody SimpleRequest<IDStringRequest> params){
        IDStringRequest account = params.getParams();
        if ((null != account) && (StringUtils.isNotBlank(account.getId()))){
            service.remove(account.getId());
        }
        SimpleResponse<IDStringRequest> ret= new SimpleResponse<IDStringRequest>();
        return  ret.success(account);
    }

    @ApiOperation(value="批量删除系统帐户",notes = "")
    @ResponseBody
    @RequestMapping(value = "/removeBatchAdminUsers", method = RequestMethod.POST)
    public SimpleResponse<BatchIDStringRequest> removeBatch(@RequestBody SimpleRequest<BatchIDStringRequest> req) {
        if ((null != req.getParams()) && (req.getParams().getIds().size() >0)){
            System.out.println(req.getParams().getIds());
            service.removeBatch(req.getParams().getIds());
        }

        SimpleResponse<BatchIDStringRequest> result = new SimpleResponse<BatchIDStringRequest>();
        return result.success(req.getParams());
    }

    @ApiOperation(value="修改系统管理用户信息")
    @PostMapping(path = "/updateAdminAccount")
    public SimpleResponse<AccountDto> updateAdminAccount (@RequestBody SimpleRequest<AccountDto> params){
        AccountDto account = params.getParams();
        account.setType(AccountType.ADMIN_USER.ordinal());
        service.updateAccount(account);
        SimpleResponse<AccountDto> ret= new SimpleResponse<AccountDto>();
        return  ret.success(account);

    }

    @ApiOperation(value="修改帐户信息")
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



    @ApiOperation(value="验证登录")
    @GetMapping(path = "/currentUser")
    public SimpleResponse<LoginResponse>  assertLoginStatus(@LoginUser SessionUser user){
        if (!user.isLoginUser()){
            throw new ServiceException(ResultCode.UN_AUTHORIZED);
        }
        AccountDto account = service.findById(user.getUserId());
        List<String> roles = service.getAccountRoles(user.getUserId());
        LoginResponse response = LoginResponse.builder().isLogin(user.isLoginUser())
                .token(user.getToken()).name(account.getNickName())
                .photoUrl(account.getPhotoUrl())
                .roles(roles).build();
        SimpleResponse<LoginResponse> result = new SimpleResponse<LoginResponse>();
        return  result.success(response);
    }
    @ApiOperation(value="微信登录及验证")
    @PostMapping(path = "/wechatLogin")
    public SimpleResponse<LoginResponse>  wechatLogin (@RequestBody SimpleRequest<WechatLoginRequest> request, HttpServletResponse response){
        String code = request.getParams().getCode();
        String token = service.wechatLogin(code);
        String domainName = appProps.getDomainName();
        Sessions.writeToken(token,domainName,true,response);
        SimpleResponse<LoginResponse> result = new SimpleResponse<LoginResponse>();
        return  result.success(LoginResponse.builder().token(token).build());

    }

    @ApiOperation(value="微信验证是否登录")
    @PostMapping(path = "/assertWechatLogin")
    public SimpleResponse<LoginResponse>  assertWechatLogin(HttpServletRequest request){
        String token = Sessions.getAuthToken(request);
        AuthModel auth = null;
        try{
            auth = Sessions.getSessionUserInfo(token);
        }catch (Exception e){
            e.printStackTrace();
        }
        LoginResponse response = LoginResponse.builder().build();
        if ( null == auth){
            response.setLogin(false);
        }else{
            response.setLogin(true);
            response.setOpenId(auth.getOpenId());
            response.setToken(token);
        }
        SimpleResponse<LoginResponse> result = new SimpleResponse<LoginResponse>();
        return  result.success(response);

    }

    @ApiOperation(value="微信验证是否登录2--测试用服务端用户注释拦截")
    @PostMapping(path = "/assertWechatLoginStatus")
    public SimpleResponse<LoginResponse>  assertWechatLoginStatus(@LoginUser SessionUser user){

        LoginResponse response = LoginResponse.builder().isLogin(user.isLoginUser()).build();
        SimpleResponse<LoginResponse> result = new SimpleResponse<LoginResponse>();
        return  result.success(response);
    }
    @ApiOperation(value="用记注册")
    @PostMapping(path = "/signup")
    public SimpleResponse<AccountDto>  signup (@RequestBody SimpleRequest<AccountDto> request, HttpServletResponse response){
        AccountDto dto = request.getParams();
        if (null == dto){
            throw new ServiceException("参数错误");
        }
        AccountDto result = this.service.signup(dto);
        if (null == result){
            throw new ServiceException("注册失败");
        }


        System.out.println("sign account dto info ===>" +  result.toString());
        SimpleResponse<AccountDto> ret= new SimpleResponse<AccountDto>();
        return  ret.success(result);

    }
    @ApiOperation(value="普通用户登录")
    @PostMapping(path = "/login")
    public SimpleResponse<LoginResponse> login (@RequestBody SimpleRequest<LoginRequest> request, HttpServletResponse response){
        LoginRequest accountLogin = request.getParams();

        String token = service.login(accountLogin);
        if (StringUtils.isBlank(token)){
            throw new ServiceException("登录失败");
        }

        String domainName = appProps.getDomainName();
        boolean rememberMe = accountLogin.isAutoLogin();

        Sessions.writeToken(token,domainName,rememberMe,response);
        SimpleResponse<LoginResponse> result = new SimpleResponse<LoginResponse>();
        return result.success(LoginResponse.builder().token(token).build());

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
