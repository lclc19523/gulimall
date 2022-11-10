package com.lc.gulimall.member.feign;

import com.lc.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 以后调用这个方法就去nacos中找gulimall-coupon 然后再调用 /coupon/coupon/member/list方法
 */
@FeignClient("gulimall-coupon")
public interface CouponFeignService {
    @RequestMapping("/coupon/coupon/member/list")
    public R membercoupons();
}
