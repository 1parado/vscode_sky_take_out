package com.sky.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;

@Mapper
public interface DishFlavorMapper {
    @AutoFill(OperationType.INSERT)
    void insertBatch(List<DishFlavor> flavors); // 批量插入口味

    
} 