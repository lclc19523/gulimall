package com.lc.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lc.common.utils.PageUtils;
import com.lc.gulimall.product.entity.CategoryEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author lc
 * @email sunlightcs@gmail.com
 * @date 2022-11-08 14:51:58
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();

    void removMenusByIds(List<Long> asList);

    /**
     * 找到catelogId的完整路径
     * [父][子][孙]
     * @param catelogId
     * @return
     */
    Long[] findCatelogPath(Long catelogId);


    void updateCascade(CategoryEntity category);
}

