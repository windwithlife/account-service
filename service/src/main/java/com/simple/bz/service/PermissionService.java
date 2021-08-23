package com.simple.bz.service;

import com.simple.bz.dao.ContextQuery;
import com.simple.bz.dao.PermissionRepository;

import com.simple.bz.dao.RolePermissionRepository;
import com.simple.bz.dao.RoleRepository;
import com.simple.bz.dto.PermissionDto;
import com.simple.bz.dto.PermissionNewDto;
import com.simple.bz.model.PermissionModel;
import com.simple.bz.model.RoleModel;
import com.simple.bz.model.RolePermissionModel;
import com.simple.common.auth.Sessions;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PermissionService {
    private final ModelMapper modelMapper;

    private final PermissionRepository dao;
    private final ContextQuery contextQuery;
    private final RolePermissionRepository rolePermissionDao;
    private final RoleRepository roleDao;

    public PermissionModel convertToModel(PermissionDto dto){
        return this.modelMapper.map(dto, PermissionModel.class);
    }
    public List<PermissionModel> convertToModels(List<PermissionDto> dtos){
        List<PermissionModel> resultList = new ArrayList<PermissionModel>();
        for (int i=0; i < dtos.size(); i++){
            resultList.add(this.convertToModel(dtos.get(i)));
        }
        return resultList;
    }
    public PermissionDto convertToDto(PermissionModel model){
        return this.modelMapper.map(model, PermissionDto.class);
    }

    public List<PermissionDto> convertToDtos(List<PermissionModel> models){
        List<PermissionDto> resultList = new ArrayList<PermissionDto>();
        for (int i=0; i < models.size(); i++){
            resultList.add(this.convertToDto(models.get(i)));
        }
        return resultList;

    }

    public List<PermissionDto> findAll(){

        List<PermissionModel> list =   dao.findAll();
        return  this.convertToDtos(list);
    }


    public PermissionDto findById(Long id){
        PermissionModel model =  dao.findById(id).get();
        return this.convertToDto(model);
    }


    public List<PermissionDto> queryAll(){
        List<PermissionDto> list = contextQuery.findList("select * from tbl_permission", PermissionDto.class);
        return  list;
    }


    public PermissionDto save(PermissionNewDto item){
        Long roleId = item.getRoleId();
        RoleModel roleModel = null;
        if (null == roleId || roleId < 0){
            roleModel = RoleModel.builder().id(0L).name("guest").build();
        }else{
            roleModel = roleDao.findById(item.getRoleId()).orElse(null);
        }
        PermissionModel model = this.modelMapper.map(item, PermissionModel.class);
        PermissionModel newPermission  = this.dao.save(model);
        return this.convertToDto(newPermission);
    }

    public PermissionDto update(PermissionDto item){
        Long id = item.getId();
        PermissionModel model = dao.findById(id).get();
        this.modelMapper.map(item, model);

        System.out.println(model.toString());
        this.dao.save(model);
        return item;
    }

    public void remove(Long id){
        this.dao.deleteById(id);
    }

    public void  removeBatch(List<Long> ids) {
        try{
            int removeRows = this.dao.deleteBatch(ids);
            System.out.println("have remove rows number ==>"  + String.valueOf(removeRows));
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}