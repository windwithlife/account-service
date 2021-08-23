package com.simple.bz.service;

import com.simple.bz.dao.*;
import com.simple.bz.dto.MenuDto;
import com.simple.bz.dto.MenuNewDto;
import com.simple.bz.dto.PermissionNewDto;
import com.simple.bz.model.MenuModel;
import com.simple.bz.model.RoleModel;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MenuService {
    private final ModelMapper modelMapper;

    private final MenuRepository dao;
    private final ContextQuery contextQuery;
    private final RolePermissionRepository rolePermissionDao;
    private final RoleRepository roleDao;

    public MenuModel convertToModel(MenuDto dto){
        return this.modelMapper.map(dto, MenuModel.class);
    }
    public List<MenuModel> convertToModels(List<MenuDto> dtos){
        List<MenuModel> resultList = new ArrayList<MenuModel>();
        for (int i=0; i < dtos.size(); i++){
            resultList.add(this.convertToModel(dtos.get(i)));
        }
        return resultList;
    }
    public MenuDto convertToDto(MenuModel model){
        return this.modelMapper.map(model, MenuDto.class);
    }

    public List<MenuDto> convertToDtos(List<MenuModel> models){
        List<MenuDto> resultList = new ArrayList<MenuDto>();
        for (int i=0; i < models.size(); i++){
            resultList.add(this.convertToDto(models.get(i)));
        }
        return resultList;

    }

    public List<MenuDto> findAll(){

        List<MenuModel> list =   dao.findAll();
        return  this.convertToDtos(list);
    }


    public MenuDto findById(Long id){
        MenuModel model =  dao.findById(id).get();
        return this.convertToDto(model);
    }


    public List<MenuDto> queryAll(){
        List<MenuDto> list = contextQuery.findList("select * from tbl_menu", MenuDto.class);
        return  list;
    }


    public MenuDto save(MenuNewDto item){

        MenuModel model = this.modelMapper.map(item, MenuModel.class);
        MenuModel newMenu  = this.dao.save(model);
        return this.convertToDto(newMenu);
    }

    public MenuDto update(MenuDto item){
        Long id = item.getId();
        MenuModel model = dao.findById(id).get();
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