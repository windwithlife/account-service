package com.simple.bz.service;

import com.simple.bz.dao.AccountRepository;
import com.simple.bz.dao.AccountRoleRepository;
import com.simple.bz.dao.ContextQuery;
import com.simple.bz.dao.UserRepository;
import com.simple.bz.dto.AccountDto;
import com.simple.bz.dto.LoginRequest;
import com.simple.bz.dto.UserDto;
import com.simple.bz.model.*;
import com.simple.common.auth.Sessions;
import com.simple.common.error.ServiceException;
import com.simple.common.utils.DateUtil;
import com.simple.common.wechat.WechatHelper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AccountService {
    private final ModelMapper modelMapper;
    private final AccountRoleRepository accountRoleDao;
    private final AccountRepository dao;
    private final UserRepository userDao;
    private final ContextQuery contextQuery;

    public AccountModel convertToModel(AccountDto dto) {
        return this.modelMapper.map(dto, AccountModel.class);
    }

    public List<AccountModel> convertToModels(List<AccountDto> dtos) {
        List<AccountModel> resultList = new ArrayList<AccountModel>();
        for (int i = 0; i < dtos.size(); i++) {
            resultList.add(this.convertToModel(dtos.get(i)));
        }
        return resultList;
    }

    public AccountDto convertToDto(AccountModel model) {
        return this.modelMapper.map(model, AccountDto.class);
    }

    public List<AccountDto> convertToDtos(List<AccountModel> models) {
        List<AccountDto> resultList = new ArrayList<AccountDto>();
        for (int i = 0; i < models.size(); i++) {
            resultList.add(this.convertToDto(models.get(i)));
        }
        return resultList;

    }

    @Transactional
    public String wechatLogin(String code) {
        String openId = WechatHelper.getWechatOpenId(code);
        System.out.println("OpenId---->" + openId);

        List<AccountModel> matchModels = dao.findByOpenId(openId);

        if (null == matchModels || matchModels.size() <= 0) {
            AccountModel model = AccountModel.builder().openId(openId).type(AccountType.WECHAT_MINI_PROGRAM.ordinal()).build();
            AccountModel newModel = dao.save(model);
            UserModel userModel = UserModel.builder().userId(newModel.getId()).build();
            UserModel newUserModel = userDao.save(userModel);
            System.out.println("new account--->" + newModel.toString() + "UserInfo--->" + newUserModel.toString());
            String rolesString = this.getAccountRolesString(model.getId());
            String token = Sessions.createTokenWithUserInfo(newModel.getId(), rolesString, openId, "");
            return token;
        } else {
            AccountModel oldModel = matchModels.get(0);
            String token = Sessions.createTokenWithUserInfo(oldModel.getId(), "guest", openId, "");
            return token;
        }


    }

    @Transactional
    public AccountDto signup(AccountDto account) {
        AccountModel model = dao.findOneByName(account.getName());
        if (null != model) {
            throw new ServiceException("该用户已存在");
        } else {
            model = this.convertToModel(account);
            model.setType(AccountType.NORMAL.ordinal());
            AccountModel newAccount = this.dao.save(model);
            this.accountRoleDao.save(AccountRoleModel.builder().accountId(newAccount.getId()).roleId(RoleModel.DEFAULT_ROLE_ID).build());
            System.out.println("sign account model info ===>" + model.toString());
            return this.convertToDto(model);
        }
    }

    @Transactional
    public String login(LoginRequest accountLogin) {
        AccountModel model = null;
        String password = "";
        String loginType = accountLogin.getType();
        if (loginType.equalsIgnoreCase("account")) {
            model = dao.findOneByName(accountLogin.getUsername());
            if (null == model) {
                throw new ServiceException("该用户不存在");
            }
            password = model.getPassword();

        }
        if (loginType.equalsIgnoreCase("mobile")) {
            model = dao.findOneByPhoneNumber(accountLogin.getMobile());
            if (null == model) {
                throw new ServiceException("该用户不存在");
            }
            password = model.getPassword();
        }

        if (StringUtils.isBlank(password)) {
            throw new ServiceException("密码异常");
        }
        if (!password.equals(accountLogin.getPassword())) {
            throw new ServiceException("密码不正确");
        }

        String rolesString = this.getAccountRolesString(model.getId());
        String token = Sessions.createTokenWithUserInfo(model.getId(), rolesString, "", "");
        return token;


    }

    public List<String> getAccountRoles(String accountId) {
        List<String> roleList = new ArrayList<String>();
        roleList.add(RoleModel.DEFAULT_ROLE_NAME);
        try{
            String querySql = "select a.id,r.name from tbl_account_role a left outer join  tbl_role r on  a.roleId= r.id where  accountId='" + accountId + "'";
            List<RoleModel> roles = contextQuery.findList(querySql, RoleModel.class);


            Iterator<RoleModel> iter = roles.iterator();
            while (iter.hasNext()) {
                RoleModel s = (RoleModel) iter.next();
                roleList.add(s.getName());
            }

            System.out.println("Current account role is==>" + roleList.toString());

            return roleList;

        }catch (Exception e){
            return  null;
        }


    }
    public String getAccountRolesString(String accountId) {
        StringBuffer rolesString = new StringBuffer();
        rolesString.append(RoleModel.DEFAULT_ROLE_NAME + ",");
        try{
            String querySql = "select a.id,r.name from tbl_account_role a left outer join  tbl_role r on  a.roleId= r.id where  accountId='" + accountId + "'";
            List<RoleModel> roles = contextQuery.findList(querySql, RoleModel.class);
            Iterator<RoleModel> iter = roles.iterator();
            while (iter.hasNext()) {
                RoleModel s = (RoleModel) iter.next();
                rolesString.append(s.getName()).append(",");
            }
            System.out.println("Current account role is==>" + rolesString.toString());
        }catch (Exception e){
           e.printStackTrace();
        }finally {
            return rolesString.toString();
        }

    }

    @Transactional
    public AccountDto updateAccount(AccountDto dto) {
        AccountModel model = dao.findById(dto.getId()).get();
        this.modelMapper.map(dto, model);
        this.dao.save(model);
        return dto;

    }

    public UserDto updateUserInfo(UserDto dto) {
        UserModel model = userDao.findById(dto.getId()).get();
        this.modelMapper.map(dto, model);
        this.userDao.save(model);
        return dto;

    }

    public List<AccountDto> findAll() {

        List<AccountModel> list = dao.findAll();
        return this.convertToDtos(list);
    }


    public AccountDto findById(String id) {
        AccountModel model = dao.findById(id).get();
        return this.convertToDto(model);
    }

    public UserDto getUserInfoByUserId(String id) {
        UserModel model = userDao.findByUserId(id);
        if (null != model) {
            return this.modelMapper.map(model, UserDto.class);
        } else {
            return null;
        }


    }

    public List<AccountDto> queryAdminUsers() {
        String queryString = "select * from tbl_account where type=" + String.valueOf(AccountType.ADMIN_USER.ordinal());
        System.out.println("QueryAdminUsers SQL ==>" + queryString);
        List<AccountDto> list = contextQuery.findList(queryString, AccountDto.class);
        return list;
    }

    public AccountDto addAdminUser(AccountDto item) {
        AccountModel model = this.convertToModel(item);
        model.setName(item.getLoginName());
        model.setType(AccountType.ADMIN_USER.ordinal());
        AccountModel newAccount = this.dao.save(model);
        this.accountRoleDao.save(AccountRoleModel.builder().accountId(newAccount.getId()).roleId(RoleModel.DEFAULT_ROLE_ID).build());
        System.out.println("sign account model info ===>" + model.toString());
        return item;
    }


    public AccountDto save(AccountDto item) {
        AccountModel model = this.convertToModel(item);
        this.dao.save(model);
        return item;
    }


    public AccountDto update(AccountDto item) {
        String id = item.getId();
        AccountModel model = dao.findById(id).get();
        this.modelMapper.map(item, model);

        System.out.println(model.toString());
        this.dao.save(model);
        return item;
    }

    public boolean remove(String id) {
        AccountModel model = this.dao.findById(id).orElse(null);
        if (AccountModel.isAdministrator(model.getName())) {
            throw new ServiceException("Can't delete the administrator user");
        }
        try {

            this.dao.deleteById(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public void removeBatch(List<String> ids) {
        AccountModel model = this.dao.findOneByName(AccountModel.ADMIN_USER_NAME);
        if (null == model) {
            return;
        }
        String adminUserId = model.getId();
        if (ids.contains(adminUserId) || ids.contains(adminUserId)) {
            throw new ServiceException("Admin user are not permitted to remove!");
        }
        try {
            int removeRows = this.dao.deleteBatch(ids);
            System.out.println("have remove rows number ==>" + String.valueOf(removeRows));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void loadAdministratorUser() {
        //保证库里有可用的缺省用户及超级用户
        List<AccountModel> superModels = dao.findByName(AccountModel.ADMIN_USER_NAME);
        if ((null == superModels) || (superModels.size() <= 0)) {
            AccountModel adminModel = AccountModel.builder().name(AccountModel.ADMIN_USER_NAME)
                    .password(AccountModel.ADMIN_USER_NAME)
                    .nickName("Administrator").type(AccountType.ADMIN_USER.ordinal()).build();
            AccountModel newModel = dao.save(adminModel);
            accountRoleDao.save(AccountRoleModel.builder().accountId(newModel.getId()).roleId(RoleModel.ADMIN_ROLE_ID).build());
            userDao.save(UserModel.builder().userId(newModel.getId()).createdDate(DateUtil.getDateToday()).updatedDate(DateUtil.getDateToday()).build());
        }


    }

}