package com.lc.gulimall.coupon.dao;

import com.lc.gulimall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author lc
 * @email sunlightcs@gmail.com
 * @date 2022-11-08 16:22:57
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
