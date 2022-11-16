package com.lc.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lc.common.utils.PageUtils;
import com.lc.gulimall.product.entity.AttrEntity;
import com.lc.gulimall.product.vo.AttrGroupRelationVo;
import com.lc.gulimall.product.vo.AttrRespVo;
import com.lc.gulimall.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author lc
 * @email sunlightcs@gmail.com
 * @date 2022-11-08 14:51:58
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);



    void saveAttr(AttrVo attr);

    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type);

    AttrRespVo getAttrInfo(Long attrId);

    void updateAttr(AttrVo attr);

    List<AttrEntity> getRelationAttr(Long attrgroupId);

    void deleteRelation(AttrGroupRelationVo[] attrGroupRelationVos);


    PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId);
}

