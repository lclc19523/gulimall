package com.lc.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.lc.gulimall.product.entity.ProductAttrValueEntity;
import com.lc.gulimall.product.service.ProductAttrValueService;
import com.lc.gulimall.product.vo.AttrRespVo;
import com.lc.gulimall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.lc.gulimall.product.entity.AttrEntity;
import com.lc.gulimall.product.service.AttrService;
import com.lc.common.utils.PageUtils;
import com.lc.common.utils.R;



/**
 * 商品属性
 *
 * @author lc
 * @email sunlightcs@gmail.com
 * @date 2022-11-08 15:46:05
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;
    ///product/attr/update/{spuId}\
    //修改商品规格
    @PostMapping("/update/{spuId}")
    public R updateBySpuId(@PathVariable("spuId") Long spuId,@RequestBody   List<ProductAttrValueEntity> entities){
        productAttrValueService.updateSpuAttr(entities,spuId);
        return R.ok().put("data",entities);
    }





    ///product/attr/base/listforspu/{spuId}
    //获取商品规格

    @GetMapping("/base/listforspu/{spuId}")
    public R baseAttrlistforspu(@PathVariable("spuId") Long spuId){
        List<ProductAttrValueEntity>    entities= productAttrValueService.baseAttrlistforspu(spuId);
        return R.ok().put("data",entities);
    }



    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }

    ///product/attr/base/list/{catelogId}



    @GetMapping("/{attrType}/list/{catelogId}")
    public R baseAttrList(@RequestParam Map<String, Object> params,
                          @PathVariable("catelogId") Long catelogId,
                          @PathVariable("attrType")  String type){
       PageUtils page=attrService.queryBaseAttrPage(params,catelogId,type);


        return R.ok().put("page",page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    //@RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId){
//		AttrEntity attr = attrService.getById(attrId);
		AttrRespVo attrRespVo= attrService.getAttrInfo(attrId);

        return R.ok().put("attr", attrRespVo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrVo attr){
		attrService.saveAttr(attr);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrVo attr){
		attrService.updateAttr(attr);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
