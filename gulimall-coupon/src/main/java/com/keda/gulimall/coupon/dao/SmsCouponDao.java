package com.keda.gulimall.coupon.dao;

import com.keda.gulimall.coupon.entity.SmsCouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author jmd
 * @email 928814697@qq.com
 * @date 2023-05-13 23:34:52
 */
@Mapper
public interface SmsCouponDao extends BaseMapper<SmsCouponEntity> {
	
}
