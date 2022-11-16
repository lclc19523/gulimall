package com.lc.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lc.common.utils.PageUtils;
import com.lc.gulimall.product.entity.SpuInfoDescEntity;
import com.lc.gulimall.product.entity.SpuInfoEntity;
import com.lc.gulimall.product.vo.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author lc
 * @email sunlightcs@gmail.com
 * @date 2022-11-08 14:51:58
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVo vo);

    void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity);

    void saveSpuInfoDesc(SpuInfoDescEntity spuInfoDescEntity);

    PageUtils queryPageByCondition(Map<String, Object> params);
}

