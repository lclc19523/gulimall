package com.lc.gulimall.product.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lc.common.utils.PageUtils;
import com.lc.common.utils.Query;

import com.lc.gulimall.product.dao.SkuInfoDao;
import com.lc.gulimall.product.entity.SkuInfoEntity;
import com.lc.gulimall.product.service.SkuInfoService;
import org.springframework.util.StringUtils;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.baseMapper.insert(skuInfoEntity);
    }

//    {
//            page: 1,//当前页码
//            limit: 10,//每页记录数
//            sidx: 'id',//排序字段
//            order: 'asc/desc',//排序方式
//            key: '华为',//检索关键字
//            catelogId: 0,
//            brandId: 0,
//            min: 0,
//            max: 0
//    }
    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();
        String key =(String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and(w->{
                w.eq("sku_id",key).or().like("sku_name",key);
            });
        }
        String catelogId =(String) params.get("catelogId");
        if(!StringUtils.isEmpty(key)&&!"0".equalsIgnoreCase(catelogId)){
            wrapper.eq("catalog_id",catelogId);

        }
        String brandId =(String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId)&&!"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id",brandId);

        }
        String min =(String) params.get("min");
        if(!StringUtils.isEmpty(catelogId)){
            wrapper.ge("price",min);
        }
        String max =(String) params.get("max");
        if(!StringUtils.isEmpty(brandId)&&!"0".equalsIgnoreCase(max)){
            wrapper.le("price",max);
        }
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

}