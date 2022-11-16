package com.lc.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lc.gulimall.product.entity.AttrEntity;
import com.lc.gulimall.product.service.AttrAttrgroupRelationService;
import com.lc.gulimall.product.service.AttrService;
import com.lc.gulimall.product.service.CategoryService;
import com.lc.gulimall.product.vo.AttrGroupRelationVo;
import com.lc.gulimall.product.vo.AttrGroupWithAttrsVo;
import com.lc.gulimall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.lc.gulimall.product.entity.AttrGroupEntity;
import com.lc.gulimall.product.service.AttrGroupService;
import com.lc.common.utils.PageUtils;
import com.lc.common.utils.R;



/**
 * 属性分组
 *
 * @author lc
 * @email sunlightcs@gmail.com
 * @date 2022-11-08 15:46:05
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;


    @Autowired
    private CategoryService categoryService;



    @Autowired
    private AttrService attrService;

    @Autowired
    private AttrAttrgroupRelationService relationService;

    /**
     * /product/attrgroup/{catelogId}/withattr
     * @param
     * @return
     */

    @GetMapping("/{catelogId}/withattr")
    public R getAttrGroupWithAttrs(@PathVariable("catelogId") Long catelogId){
        //1.查出当前分类下的所有属性分组
        //2.查出每个属性分组的所有属性

       List<AttrGroupWithAttrsVo> attrGroupWithAttrsVos=attrGroupService.getAttrGroupWithAttrsBycatelogId(catelogId);
       return R.ok().put("data",attrGroupWithAttrsVos);
    }



    //product/attrgroup/attr/relation=
    @PostMapping("/attr/relation")
    public R addRelation(@RequestBody List<AttrGroupRelationVo> vos){


        relationService.saveBatch(vos);


        return R.ok();
    }


    ///product/attrgroup/{attrgroupId}/attr/relation
    @GetMapping("/{attrgroupId}/attr/relation")
    public R attrRelation(@PathVariable("attrgroupId") Long attrgroupId){
        List<AttrEntity> entities= attrService.getRelationAttr(attrgroupId);
        return R.ok().put("data",entities);

    }
    ///product/attrgroup/{attrgroupId}/noattr/relation
    @GetMapping("/{attrgroupId}/noattr/relation")
    public R attrNoRelation(@RequestParam Map<String,Object> params, @PathVariable("attrgroupId") Long attrgroupId){
        PageUtils page= attrService.getNoRelationAttr(params,attrgroupId);
        return R.ok().put("page",page);

    }

    //product/attrgroup/attr/relation/delete
    @PostMapping("/attr/relation/delete")
    public R deleteRelation(@RequestBody AttrGroupRelationVo[] attrGroupRelationVos){
        attrService.deleteRelation(attrGroupRelationVos);

        return R.ok();


    }

    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
    //@RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params,@PathVariable("catelogId") Long catelogId){
        //PageUtils page = attrGroupService.queryPage(params);
        PageUtils page = attrGroupService.queryPage(params,catelogId);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    //@RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        Long catelogId = attrGroup.getCatelogId();

        Long[] path=categoryService.findCatelogPath(catelogId);
        attrGroup.setCatelogPath(path);

        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
