package com.lc.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.lc.common.constant.ProductConstant;
import com.lc.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.lc.gulimall.product.dao.AttrGroupDao;
import com.lc.gulimall.product.dao.CategoryDao;
import com.lc.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.lc.gulimall.product.entity.AttrGroupEntity;
import com.lc.gulimall.product.entity.CategoryEntity;
import com.lc.gulimall.product.service.CategoryService;
import com.lc.gulimall.product.vo.AttrGroupRelationVo;
import com.lc.gulimall.product.vo.AttrRespVo;
import com.lc.gulimall.product.vo.AttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lc.common.utils.PageUtils;
import com.lc.common.utils.Query;

import com.lc.gulimall.product.dao.AttrDao;
import com.lc.gulimall.product.entity.AttrEntity;
import com.lc.gulimall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.xml.crypto.Data;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {
    @Autowired
    private AttrAttrgroupRelationDao relationDao;


    @Autowired
    private AttrGroupDao attrGroupDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void saveAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr,attrEntity);
        //1.保存基本数据
        this.save(attrEntity);
        //2.保存关联关系
        if(attr.getAttrType()== ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()&&attr.getAttrGroupId()!=null){
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrId(attrEntity.getAttrId());
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationDao.insert(relationEntity);
        }



    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type) {
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("attr_type","base".equalsIgnoreCase(type)?ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode():ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());
        if(catelogId!=0){
            wrapper.eq("catelog_id",catelogId);
        }
        String key =(String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and((obj)->{
                obj.eq("attr_id",key).or().like("attr_name",key);
            });
        }
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                wrapper
        );
        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();
        List<AttrRespVo> respVos = records.stream().map((attrEntity) -> {

            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity, attrRespVo);
            //1.设置分类和分组的名字

            if("base".equalsIgnoreCase(type)){
                AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().
                        eq("attr_id", attrEntity.getAttrId()));
                if (relationEntity != null&&relationEntity.getAttrGroupId()!=null) {
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
            if (categoryEntity != null) {
                attrRespVo.setCatelogName(categoryEntity.getName());
            }
            return attrRespVo;
        }).collect(Collectors.toList());
        pageUtils.setList(respVos);
        return pageUtils;
    }

    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        AttrRespVo attrRespVo=new AttrRespVo();
        AttrEntity attrEntity = this.getById(attrId);
        BeanUtils.copyProperties(attrEntity,attrRespVo);

        if(attrEntity.getAttrType()==ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){
            //1.设置分组信息
            AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
            if(relationEntity!=null){
                attrRespVo.setAttrGroupId(relationEntity.getAttrGroupId());
            }

            AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
            if(attrGroupEntity!=null){
                attrRespVo.setAttrGroupId(relationEntity.getAttrGroupId());
            }
        }
        //2.设置分类信息
        Long catelogId = attrEntity.getCatelogId();
        Long[] catelogPath = categoryService.findCatelogPath(catelogId);
        attrRespVo.setCatelogPath(catelogPath);

        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        if(categoryEntity!=null){
            attrRespVo.setCatelogName(categoryEntity.getName());
        }

        return attrRespVo;
    }

    @Transactional
    @Override
    public void updateAttr(AttrVo attr) {

        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr,attrEntity);
        //1.基本修改
        this.updateById(attrEntity);



        Integer count = relationDao.selectCount(new QueryWrapper<AttrAttrgroupRelationEntity>().
                eq("attr_id", attr.getAttrId()));

        if(attrEntity.getAttrType()==ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){
            //2.修改关联
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrId(attr.getAttrId());
            relationEntity.setAttrGroupId(attr.getAttrGroupId());

            //如果已经有记录说明是修改
            if(count>0){
                relationDao.update(relationEntity, new UpdateWrapper<AttrAttrgroupRelationEntity>().
                        eq("attr_id",attr.getAttrId()));
            }else{
                //新增
                relationDao.insert(relationEntity);
            }
        }
    }

    /**
     * 根据分组id查找关联的所有基本属性
     * @param attrgroupId
     * @return
     */
    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        List<AttrAttrgroupRelationEntity> entities = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId));
        List<Long> attrIds = entities.stream().map((data) -> {
            return data.getAttrId();

        }).collect(Collectors.toList());
        if(attrIds==null||attrIds.size()==0){
            return null;
        }
        Collection<AttrEntity> attrEntities = this.listByIds(attrIds);
        return (List<AttrEntity>) attrEntities;


    }

    @Override
    public void deleteRelation(AttrGroupRelationVo[] attrGroupRelationVos) {
        List<AttrAttrgroupRelationEntity> entities = Arrays.asList(attrGroupRelationVos).stream().map((data) -> {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrId(data.getAttrId());
            attrAttrgroupRelationEntity.setAttrGroupId(data.getAttrGroupId());
            return attrAttrgroupRelationEntity;
        }).collect(Collectors.toList());
        relationDao.deleteBatchRelation(entities);

    }

    /**
     * 获取当前分组没有关联的所有属性
     * @param params
     * @param attrgroupId
     * @return
     */
    @Override
    public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId) {
        //1.当前分组只能关联自己所属的分类里面的所有属性
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        // 只能查这个分类下的属性
        Long catelogId = attrGroupEntity.getCatelogId();
        //2.当前分组只能关联别的分组没有引用的属性
        //2.1)当前分类下的其他分组
        List<AttrGroupEntity> group = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>().
                eq("catelog_id", catelogId)/*.ne("attr_group_id",attrgroupId)*/);
        //2.2).这些分组关联的属性
        List<Long> groupIds = group.stream().map((data) -> {
            return data.getAttrGroupId();
        }).collect(Collectors.toList());
        List<AttrAttrgroupRelationEntity> relationEntities = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", groupIds));
        List<Long> attrIds = relationEntities.stream().map((item) -> {
            return item.getAttrId();
        }).collect(Collectors.toList());
        //2.3)从当前分类的所以属性中移除这些属性
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>().eq("catelog_id", catelogId).eq("attr_type",ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if(attrIds!=null&&attrIds.size()!=0){
            wrapper.notIn("attr_id", attrIds);
        }
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and((w)->{
                w.eq("attr_id",key).or().like("attr_name",key);
            });
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), wrapper);
        return new PageUtils(page);
    }

}