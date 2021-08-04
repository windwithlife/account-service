package com.simple.bz.service;

import com.simple.bz.dao.AccountRepository;
import com.simple.bz.dao.ContextQuery;
import com.simple.bz.dao.RoleRepository;
import com.simple.bz.dao.UserRepository;
import com.simple.bz.dto.RoleDto;
import com.simple.bz.dto.UserDto;
import com.simple.bz.model.RoleModel;
import com.simple.bz.model.AccountType;
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
public class RoleService {
    private final ModelMapper modelMapper;
    private final UserRepository userDao;
    private final RoleRepository dao;
    private final ContextQuery contextQuery;

    public RoleModel convertToModel(RoleDto dto){
        return this.modelMapper.map(dto, RoleModel.class);
    }
    public List<RoleModel> convertToModels(List<RoleDto> dtos){
        List<RoleModel> resultList = new ArrayList<RoleModel>();
        for (int i=0; i < dtos.size(); i++){
            resultList.add(this.convertToModel(dtos.get(i)));
        }
        return resultList;
    }
    public RoleDto convertToDto(RoleModel model){
        return this.modelMapper.map(model, RoleDto.class);
    }

    public List<RoleDto> convertToDtos(List<RoleModel> models){
        List<RoleDto> resultList = new ArrayList<RoleDto>();
        for (int i=0; i < models.size(); i++){
            resultList.add(this.convertToDto(models.get(i)));
        }
        return resultList;

    }

    public List<RoleDto> findAll(){

        List<RoleModel> list =   dao.findAll();
        return  this.convertToDtos(list);
    }


    public RoleDto findById(Long id){
        RoleModel model =  dao.findById(id).get();
        return this.convertToDto(model);
    }


    public List<RoleDto> queryAll(){
        List<RoleDto> list = contextQuery.findList("select * from tbl_role", RoleDto.class);
        return  list;
    }


    public RoleDto save(RoleDto item){
        RoleModel model = this.convertToModel(item);
        this.dao.save(model);
        return item;
    }

    public RoleDto update(RoleDto item){
        Long id = item.getId();
        RoleModel model = dao.findById(id).get();
        this.modelMapper.map(item, model);

        System.out.println(model.toString());
        this.dao.save(model);
        return item;
    }

    public void remove(Long id){
        this.dao.deleteById(id);
    }

}