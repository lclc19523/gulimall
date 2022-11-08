package com.lc.gulimall.product.dao;

import com.lc.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author lc
 * @email sunlightcs@gmail.com
 * @date 2022-11-08 14:51:58
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
