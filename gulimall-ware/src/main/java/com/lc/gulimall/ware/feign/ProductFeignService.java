package com.lc.gulimall.ware.feign;

import com.lc.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gulimall-product")
public interface ProductFeignService {

    /**
     *           /product/spuinfo/info/{skuId}
     *           /api/product/spuinfo/info/{skuId}
     *
     *           //1.让所以请求过网关
     *                1.1).@FeignClient("glimall-gateway"):给gulimall-gateway所在的机器发请求
     *                1.2)./api/product/spuinfo/info/{skuId}
     *           //2.直接让后台指定服务处理
     *                2.1).@FeignClient("glimall-product")
     *                2.2)./product/spuinfo/info/{skuId}
     *
     *
     *
     * @param skuId
     * @return
     */

    @RequestMapping("/product/spuinfo/info/{skuId}")
    //@RequiresPermissions("product:skuinfo:info")
    public R info(@PathVariable("skuId") Long skuId);
}
