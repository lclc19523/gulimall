package com.lc.gulimall.ware.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lc.common.utils.PageUtils;
import com.lc.common.utils.Query;

import com.lc.gulimall.ware.dao.PurchaseDetailDao;
import com.lc.gulimall.ware.entity.PurchaseDetailEntity;
import com.lc.gulimall.ware.service.PurchaseDetailService;
import org.springframework.util.StringUtils;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
//        key:123
//        status:1
//        wareId:3

        QueryWrapper<PurchaseDetailEntity> wrapper = new QueryWrapper<>();
        String key =(String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            //id  purchase_id sku_id
            wrapper.and(w->{
                w.eq("id",key)
                        .or().eq("purchase_id",key)
                        .or().eq("sku_id",key);
            });
        }

        String status =(String) params.get("status");
        if(!StringUtils.isEmpty(status)){
            wrapper.eq("status",status);
        }
        String wareId =(String) params.get("wareId");
        if(!StringUtils.isEmpty(wareId)){
            wrapper.eq("ware_id",wareId);
        }
        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                wrapper

        );

        return new PageUtils(page);
    }

}