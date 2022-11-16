package com.lc.gulimall.ware.dao;

import com.lc.gulimall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商品库存
 * 
 * @author lc
 * @email sunlightcs@gmail.com
 * @date 2022-11-08 17:16:47
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    void addStock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum, @Param("wareId") Long wareId);
}
