package com.simple.bz.service;

import com.simple.bz.dao.AccountRepository;
import com.simple.bz.dao.ContextQuery;
import com.simple.bz.dao.UserRepository;
import com.simple.bz.dto.AccountDto;
import com.simple.bz.dto.UserDto;
import com.simple.bz.model.AccountModel;
import com.simple.bz.model.UserModel;
import com.simple.common.auth.Sessions;
import com.simple.common.error.ServiceException;
import com.simple.common.wechat.WechatHelper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AccountService {
    private final ModelMapper modelMapper;
    
    private final AccountRepository dao;
    private final UserRepository userDao;
    private final ContextQuery contextQuery;

    public AccountModel convertToModel(AccountDto dto){
        return this.modelMapper.map(dto, AccountModel.class);
    }
    public List<AccountModel> convertToModels(List<AccountDto> dtos){
        List<AccountModel> resultList = new ArrayList<AccountModel>();
        for (int i=0; i < dtos.size(); i++){
            resultList.add(this.convertToModel(dtos.get(i)));
        }
        return resultList;
    }
    public AccountDto convertToDto(AccountModel model){
        return this.modelMapper.map(model, AccountDto.class);
    }

    public List<AccountDto> convertToDtos(List<AccountModel> models){
        List<AccountDto> resultList = new ArrayList<AccountDto>();
        for (int i=0; i < models.size(); i++){
            resultList.add(this.convertToDto(models.get(i)));
        }
        return resultList;

    }

    public String wechatLogin(String code ){
        String openId = WechatHelper.getWechatOpenId(code);
        String token = Sessions.createTokenWithUserInfo(openId, "guest", openId, "");
        return token;
    }

    public AccountDto signup(AccountDto account){
        AccountModel model = dao.findOneByName(account.getName());
        if (null != model){
            throw new ServiceException("该用户已存在");
        }else{
            model = this.convertToModel(account);
            this.dao.save(model);
            System.out.println("sign account model info ===>" +  model.toString());
            return this.convertToDto(model);
        }
    }
    public String login(AccountDto account){
        AccountModel model = dao.findOneByName(account.getName());
        String token = "";
        if (null == model){
            throw new ServiceException("该用户不存在");
        }else{
           if(model.getPassword().equals(account.getPassword())){
               token = Sessions.createTokenWithUserInfo(account.getId(), "guest", "", "");
               return token;
           }else{
               throw new ServiceException("用户密码不正确");
           }
        }
    }

    public AccountDto updateAccount(AccountDto dto){
        AccountModel model =  dao.findById(dto.getId()).get();
        this.modelMapper.map(dto, model);
        this.dao.save(model);
        return dto;

    }

    public UserDto updateUserInfo(UserDto dto){
        UserModel model =  userDao.findById(dto.getId()).get();
        this.modelMapper.map(dto, model);
        this.userDao.save(model);
        return dto;

    }
    public List<AccountDto> findAll(){

        List<AccountModel> list =   dao.findAll();
        return  this.convertToDtos(list);
    }


    public AccountDto findById(String id){
        AccountModel model =  dao.findById(id).get();
        return this.convertToDto(model);
    }
    public UserDto getUserInfoByUserId(String id){
        UserModel model =  userDao.findByUserId(id);
        return this.modelMapper.map(model, UserDto.class);
    }

    public List<AccountDto> queryAll(){
        List<AccountDto> list = contextQuery.findList("select * from tbl_account", AccountDto.class);
        return  list;
    }



    public AccountDto save(AccountDto item){
        AccountModel model = this.convertToModel(item);
        this.dao.save(model);
        return item;
    }


    public AccountDto update(AccountDto item){
        String id = item.getId();
        AccountModel model = dao.findById(id).get();
        this.modelMapper.map(item, model);

        System.out.println(model.toString());
        this.dao.save(model);
        return item;
    }

    public void remove(String id){
        this.dao.deleteById(id);
    }

}