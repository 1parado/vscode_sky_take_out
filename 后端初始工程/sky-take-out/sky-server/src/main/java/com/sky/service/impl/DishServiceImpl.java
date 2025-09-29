package com.sky.service.impl;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class DishServiceImpl implements DishService{

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
   
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
       Dish dish = new Dish();
       BeanUtils.copyProperties(dishDTO, dish); // 将dishDTO 传给dish对象（副本）
       
       dishMapper.insert(dish); 
       Long dishId = dish.getId(); // 获取insert语句生成的主键值 菜品中的id = 口味中的dishId
       
       List<DishFlavor> flavors = dishDTO.getFlavors(); //口味可能有多种 所以是List<DishFlavor>
       if(flavors!=null && flavors.size()>0){
        flavors.forEach(DishFlavor->{DishFlavor.setDishId(dishId);}); // 设置外键
        // 向口味插入n条数据
        dishFlavorMapper.insertBatch(flavors);
       }

    }
    
    
}
