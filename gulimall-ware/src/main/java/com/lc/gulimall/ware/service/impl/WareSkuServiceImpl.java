package com.lc.gulimall.ware.service.impl;

import com.lc.common.utils.R;
import com.lc.gulimall.ware.feign.ProductFeignService;
import com.lc.gulimall.ware.to.skuNameTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lc.common.utils.PageUtils;
import com.lc.common.utils.Query;

import com.lc.gulimall.ware.dao.WareSkuDao;
import com.lc.gulimall.ware.entity.WareSkuEntity;
import com.lc.gulimall.ware.service.WareSkuService;
import org.springframework.util.StringUtils;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    private ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
            //    wareId: 123,//仓库id
            //    skuId: 123//商品id
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();
        String skuId =(String) params.get("skuId");
        if(!StringUtils.isEmpty(skuId)){
            wrapper.eq("sku_id",skuId);
        }
        String wareId =(String) params.get("wareId");
        if(!StringUtils.isEmpty(wareId)){
            wrapper.eq("ware_id",wareId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper

        );
        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Integer skuNum, Long wareId) {
        //1.判断如果还没有这个库存记录新增操作
        List<WareSkuEntity> wareSkuEntities = this.baseMapper.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if(wareSkuEntities==null||wareSkuEntities.size()==0){
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStockLocked(0);

            //TODO 远程查询sku的名字，如果失败，整个事务无需回滚  有问题
            //1.自己带catch异常 处理
//            try {
//                R info = productFeignService.info(skuId);
//                Map<String,Object> data =(Map<String, Object>) info.get("skuInfo");
//                if(info.getCode()==0){
//                    wareSkuEntity.setSkuName((String) data.get("skuName"));
//                }
//            }catch(Exception e){
//                System.out.println(e);
//            }
            this.baseMapper.insert(wareSkuEntity);
        }else {
            this.baseMapper.addStock(skuId,skuNum,wareId);
        }


    }

}