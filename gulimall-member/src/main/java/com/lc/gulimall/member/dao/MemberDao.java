package com.lc.gulimall.member.dao;

import com.lc.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author lc
 * @email sunlightcs@gmail.com
 * @date 2022-11-08 16:29:34
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
