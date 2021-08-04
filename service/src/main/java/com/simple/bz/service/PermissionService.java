package com.simple.bz.service;

import com.simple.bz.dao.ContextQuery;
import com.simple.bz.dao.PermissionRepository;

import com.simple.bz.dto.PermissionDto;
import com.simple.bz.model.PermissionModel;
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
        List<PermissionDto> list = contextQuery.findList("select * from tbl_role", PermissionDto.class);
        return  list;
    }


    public PermissionDto save(PermissionDto item){
        PermissionModel model = this.convertToModel(item);
        this.dao.save(model);
        return item;
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

}