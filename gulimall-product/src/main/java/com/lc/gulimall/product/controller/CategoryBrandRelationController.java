package com.lc.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lc.gulimall.product.entity.BrandEntity;
import com.lc.gulimall.product.vo.BrandVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.lc.gulimall.product.entity.CategoryBrandRelationEntity;
import com.lc.gulimall.product.service.CategoryBrandRelationService;
import com.lc.common.utils.PageUtils;
import com.lc.common.utils.R;



/**
 * 品牌分类关联
 *
 * @author lc
 * @email sunlightcs@gmail.com
 * @date 2022-11-08 15:46:05
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;


    /**product/categorybrandrelation/brands/list
     * 1.Controller:处理请求,接受和校验数据
     * 2.Service接受controller传来的数据，进行业务处理
     * 3.Controoler接受Service处理完的数据，封装页面指定的vo
     */


    @GetMapping("/brands/list")
    public R realtionBrandsList(@RequestParam(value = "catId",required = true) Long catId){


        List<BrandEntity> brandEntities=categoryBrandRelationService.getBrandsByCatId(catId);
        List<BrandVo> collect = brandEntities.stream().map((data) -> {
            BrandVo brandVo = new BrandVo();
            brandVo.setBrandId(data.getBrandId());
            brandVo.setBrandName(data.getName());
            return brandVo;
        }).collect(Collectors.toList());
        return R.ok().put("data",collect);
    }

    /**
     * 获取当前品牌关联的所有分类列表
     */
//    @RequestMapping(value = "/catelog/list",method = RequestMethod.GET)
    @GetMapping("/catelog/list")
    //@RequiresPermissions("product:categorybrandrelation:list")
    public R catelogList(@RequestParam("brandId") Long brandId){
        List<CategoryBrandRelationEntity> data=categoryBrandRelationService.list(
                new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id",brandId));
        return R.ok().put("data", data);
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:categorybrandrelation:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("product:categorybrandrelation:info")
    public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:categorybrandrelation:save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.saveDetail(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:categorybrandrelation:update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:categorybrandrelation:delete")
    public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
