package com.lc.gulimall.ware.service.impl;

import com.lc.common.constant.WareConstant;
import com.lc.gulimall.ware.entity.PurchaseDetailEntity;
import com.lc.gulimall.ware.service.PurchaseDetailService;
import com.lc.gulimall.ware.service.WareSkuService;
import com.lc.gulimall.ware.vo.MergeVo;
import com.lc.gulimall.ware.vo.PurchaseDoneVo;
import com.lc.gulimall.ware.vo.PurchaseItemDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lc.common.utils.PageUtils;
import com.lc.common.utils.Query;

import com.lc.gulimall.ware.dao.PurchaseDao;
import com.lc.gulimall.ware.entity.PurchaseEntity;
import com.lc.gulimall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {


    @Autowired
    private PurchaseDetailService purchaseDetailService;


    @Autowired
    private WareSkuService wareSkuService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceivePurchase(Map<String, Object> params) {
        QueryWrapper<PurchaseEntity> wrapper = new QueryWrapper<>();

        wrapper.eq("status",0).or().eq("status",1);


        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                wrapper

        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void mergePurchase(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        if(purchaseId==null){
            //????????????id ???????????????
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            this.save(purchaseEntity);
            purchaseId=purchaseEntity.getId();
        }
        // TODO ???????????????????????????0???1????????????
        PurchaseEntity byId = this.getById(purchaseId);

        if(byId.getStatus()!=WareConstant.PurchaseStatusEnum.CREATED.getCode()&&byId.getStatus()!=WareConstant.PurchaseStatusEnum.ASSIGNED.getCode()){
            return;
        }
        List<Long> items = mergeVo.getItems();
        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> collect = items.stream().map(item -> {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            purchaseDetailEntity.setStatus(WareConstant.PurchaseStatusEnum.ASSIGNED.getCode());
            purchaseDetailEntity.setId(item);
            purchaseDetailEntity.setPurchaseId(finalPurchaseId);
            purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());

            return purchaseDetailEntity;
        }).collect(Collectors.toList());
        purchaseDetailService.updateBatchById(collect);
        PurchaseEntity purchaseEntity=new PurchaseEntity();
        purchaseEntity.setUpdateTime(new Date());
        purchaseEntity.setId(finalPurchaseId);
        this.updateById(purchaseEntity);
    }

    @Transactional
    @Override
    public void received(List<Long> ids) {
        //1.??????????????????????????????????????????????????????
        List<PurchaseEntity> purchaseEntities = ids.stream().map(id -> {
            PurchaseEntity purchaseEntity = this.getById(id);
            return purchaseEntity;
        }).filter(item->{
            return item.getStatus()==WareConstant.PurchaseStatusEnum.CREATED.getCode()||item.getStatus()==WareConstant.PurchaseStatusEnum.ASSIGNED.getCode();
        }).map(item->{
            item.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
            return item;
        }).collect(Collectors.toList());



        //2.????????????????????????


        this.updateBatchById(purchaseEntities);

        //3.????????????????????????
        purchaseEntities.forEach(item->{
            List<PurchaseDetailEntity> purchaseDetailEntities = purchaseDetailService.list(new QueryWrapper<PurchaseDetailEntity>().eq("purchase_id", item.getId()));
            List<PurchaseDetailEntity> collect = purchaseDetailEntities.stream().map((detail) -> {
                detail.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
                return detail;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(collect);
        });
    }

    @Transactional
    @Override
    public void done(PurchaseDoneVo doneVo) {





        //2.????????????????????????
        Boolean flag=true;
        List<PurchaseDetailEntity> updates=new ArrayList<>();
        List<PurchaseItemDoneVo> items = doneVo.getItems();
        for (PurchaseItemDoneVo item : items) {
            PurchaseDetailEntity purchaseDetailEntity=new PurchaseDetailEntity();
            if(item.getStatus()==WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode()) {
                flag = false;
                purchaseDetailEntity.setStatus(item.getStatus());
            }else{
                purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.FINISH.getCode());

                //3.????????????????????????
                //3.1????????????????????????
                PurchaseDetailEntity byId = purchaseDetailService.getById(item.getItemId());

                wareSkuService.addStock(byId.getSkuId(),byId.getSkuNum(),byId.getWareId());


            }
            purchaseDetailEntity.setId(item.getItemId());
            updates.add(purchaseDetailEntity);
        }
        purchaseDetailService.updateBatchById(updates);




        //1.????????????????????????
        PurchaseEntity purchaseEntity = new PurchaseEntity();

        if(flag){
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.FINISH.getCode());
        }else{
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.HASERROR.getCode());
        }
        purchaseEntity.setId(doneVo.getId());
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);











    }

}