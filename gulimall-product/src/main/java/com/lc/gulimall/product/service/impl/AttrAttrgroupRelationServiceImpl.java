package com.lc.gulimall.product.service.impl;

import com.lc.gulimall.product.vo.AttrGroupRelationVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lc.common.utils.PageUtils;
import com.lc.common.utils.Query;

import com.lc.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.lc.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.lc.gulimall.product.service.AttrAttrgroupRelationService;


@Service("attrAttrgroupRelationService")
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrgroupRelationDao, AttrAttrgroupRelationEntity> implements AttrAttrgroupRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrAttrgroupRelationEntity> page = this.page(
                new Query<AttrAttrgroupRelationEntity>().getPage(params),
                new QueryWrapper<AttrAttrgroupRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveBatch(List<AttrGroupRelationVo> vos) {

        List<AttrAttrgroupRelationEntity> collect = vos.stream().map((data) -> {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrId(data.getAttrId());
            attrAttrgroupRelationEntity.setAttrGroupId(data.getAttrGroupId());
            return attrAttrgroupRelationEntity;
        }).collect(Collectors.toList());
        this.saveBatch(collect);

    }

}