package com.lc.gulimall.product.service.impl;

import com.lc.gulimall.product.entity.AttrEntity;
import com.lc.gulimall.product.service.AttrService;
import com.lc.gulimall.product.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lc.common.utils.PageUtils;
import com.lc.common.utils.Query;

import com.lc.gulimall.product.dao.AttrGroupDao;
import com.lc.gulimall.product.entity.AttrGroupEntity;
import com.lc.gulimall.product.service.AttrGroupService;
import org.springframework.util.StringUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrService attrService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        //查所有
        String key=(String) params.get("key");
        //select * from pms_attr_group where catelog_id=? and (attr_group_id=key or attr_group_name like %key% )
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>();
        if(!StringUtils.isEmpty(key)){
            wrapper.and((obj)->{
                obj.eq("attr_group_id",key).or().like("attr_group_name",key);
            });
        }
        if(catelogId==0){
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),
                    wrapper);
            return new PageUtils(page);
        }else{
            wrapper.eq("catelog_id",catelogId);
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper);
            return new PageUtils(page);
        }
    }

    /**
     * 根据分类id查出所有的分组以及这些分组带的属性
     * @param catelogId
     * @return
     */

    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsBycatelogId(Long catelogId) {
        //1.查询分组信息
        List<AttrGroupEntity> attrGroupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));


        //2.查询所有属性
        List<AttrGroupWithAttrsVo> collect = attrGroupEntities.stream().map((groupEntity) -> {
            //1.给vo封装group属性
            AttrGroupWithAttrsVo attrGroupWithAttrsVo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(groupEntity, attrGroupWithAttrsVo);
            //2.查新group对应的基本属性
            List<AttrEntity> attrEntities = attrService.getRelationAttr(groupEntity.getAttrGroupId());
            attrGroupWithAttrsVo.setAttrs(attrEntities);
            return attrGroupWithAttrsVo;
        }).collect(Collectors.toList());
        return collect;
    }
}