package com.lc.gulimall.product.service.impl;

import com.lc.common.to.SkuReductionTo;
import com.lc.common.to.SpuBoundTo;
import com.lc.common.to.es.SkuEsModel;
import com.lc.common.utils.R;
import com.lc.gulimall.product.dao.SpuInfoDescDao;
import com.lc.gulimall.product.entity.*;
import com.lc.gulimall.product.feign.CouponFeignService;
import com.lc.gulimall.product.service.*;
import com.lc.gulimall.product.vo.*;
import com.sun.javafx.scene.shape.PathUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lc.common.utils.PageUtils;
import com.lc.common.utils.Query;

import com.lc.gulimall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private SpuImagesService spuImagesService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }
    //TODO 高级部分完善
//    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo vo) {

        //1.保存spu基本信息 pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(spuInfoEntity);

        //2.保存spu的描述图片 pms_spu_info_desc
        List<String> decript = vo.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescEntity.setDecript(String.join(",", decript));
        spuInfoDescService.saveSpuInfoDesc(spuInfoDescEntity);
        //3.保存spu的图片集 pms_spu_images
        List<String> images = vo.getImages();
        spuImagesService.saveImages(spuInfoEntity.getId(), images);
        //4.保存spu的规格参数；pms_product_attr_value
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(item -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            productAttrValueEntity.setAttrId(item.getAttrId());
            productAttrValueEntity.setAttrValue(item.getAttrValues());
            productAttrValueEntity.setQuickShow(item.getShowDesc());
            productAttrValueEntity.setSpuId(spuInfoEntity.getId());
            //属性名的查询
            AttrEntity attrEntity = attrService.getById(item.getAttrId());
            productAttrValueEntity.setAttrName(attrEntity.getAttrName());
            return productAttrValueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveProductAttr(collect);


        //5.保存spu的积分信息 gulimall_sms-》sms_spu_bounds
        Bounds bounds = vo.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds, spuBoundTo);
        spuBoundTo.setSpuId(spuInfoEntity.getId());
        R r = couponFeignService.saveSpuBounds(spuBoundTo);
        if(r.getCode()!=0){
            log.error("远程保存spu积分信息失败");
        }


        //5.保存当前spu对应的所有sku信息；
        List<Skus> skus = vo.getSkus();
        if (skus != null && skus.size() > 0) {
            skus.forEach(item -> {
                //找默认图片
                String defaultImg = "";
                for (Images image : item.getImages()) {
                    if (image.getDefaultImg() == 1) {
                        defaultImg = image.getImgUrl();
                    }
                }
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
//                private String skuName;
//                private BigDecimal price;
//                private String skuTitle;
//                private String skuSubtitle;
                BeanUtils.copyProperties(item, skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0l);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                //设置默认图片
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                //5.1保存sku的基本信息  pms_sku_info
                skuInfoService.saveSkuInfo(skuInfoEntity);
                Long skuId = skuInfoEntity.getSkuId();

                List<SkuImagesEntity> imagesEntities = item.getImages().stream().map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    return skuImagesEntity;
                }).filter(entity->{
                    //返回true就是需要，返回false机就会被过滤掉
                    return !StringUtils.isEmpty(entity.getImgUrl());
                }).collect(Collectors.toList());
                //5.2.sku的图片信息 pms_sku_images
                skuImagesService.saveBatch(imagesEntities);
                //5.3.sku的销售属性信息  pms_sku_sale_attr_value
                List<Attr> attr = item.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attr.stream().map(a -> {
                    SkuSaleAttrValueEntity saleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(a, saleAttrValueEntity);
                    saleAttrValueEntity.setSkuId(skuInfoEntity.getSkuId());
                    return saleAttrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);

                //5.4.sku的优惠满减等信息 gulimall_sms-》sms_sku_ladder。sms_sku_full_reduction。sms_member_price
                SkuReductionTo skuReductionTo = new SkuReductionTo();


                BeanUtils.copyProperties(item, skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                if(skuReductionTo.getFullCount()>0||skuReductionTo.getFullPrice().compareTo(new BigDecimal("0"))==1){
                    R r1 = couponFeignService.saveSkuReduction(skuReductionTo);
                    if(r1.getCode()!=0){
                        log.error("远程保存sku的优惠满减等信息失败");
                    }
                }

            });
        }
    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }

    @Override
    public void saveSpuInfoDesc(SpuInfoDescEntity spuInfoDescEntity) {


    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(params.get("key"))){
            wrapper.and((w)->{
                w.eq("id",key).or().like("spu_name",key);
            });
        }
        // status=1 and (id=1 or spu_name like xxx) 这种内嵌的要用and
        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(params.get("status"))){
            wrapper.eq("publish_status",status);

        }
        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(params.get("brandId"))){
            wrapper.eq("brand_id",brandId);


        }
        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(params.get("catelogId"))){
            wrapper.eq("catalog_id",catelogId);
        }

        if (!StringUtils.isEmpty(params.get("key"))){


        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void up(Long spuId) {

        List<SkuEsModel> upProducts=new ArrayList<>();
        //1.组装需要的数据

        //1.查出当前spuid对应的所有sku信息，品牌的名字
        List<SkuInfoEntity> skus=skuInfoService.getSkusBySpuId(spuId);


        //TODO 4.查询当前sku的所有可以被用来检索的规格属性，
        List<ProductAttrValueEntity> entities = productAttrValueService.baseAttrlistforspu(spuId);
        List<Long> attrIds = entities.stream().map(ProductAttrValueEntity::getAttrId).collect(Collectors.toList());
        List<Long>searchAttrIds=attrService.selectSearchAttrIds(attrIds);

        Set<Long>




        //2.封装每个sku的信息
        skus.stream().map(sku->{
            SkuEsModel esModel=new SkuEsModel();
            BeanUtils.copyProperties(sku,esModel);
            /**skuPrice,skuImg,hasStock,hotScore
             *brandName,brandImg,catalogName
             * attrs
             * private Long attrId;
             *         private String attrName;
             *         private String attrValue;
             *
             *
             */
            esModel.setSkuPrice(sku.getPrice());
            esModel.setSkuImg(sku.getSkuDefaultImg());
            //TODO 1.发送远程调用，库存系统查询是否有库存
            //TODO 2.热度评分，0
            //TODO 3.查询品牌的分类和名字信息
            //brandName,brandImg,catalogName
            BrandEntity brandEntity = brandService.getById(esModel.getBrandId());
            esModel.setBrandName(brandEntity.getName());
            esModel.setBrandImg(brandEntity.getLogo());
            CategoryEntity categoryEntity = categoryService.getById(esModel.getCatalogId());
            esModel.setCatalogName(categoryEntity.getName());




            return esModel;
        });




    }

}