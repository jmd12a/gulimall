package com.keda.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.keda.common.utils.PageUtils;
import com.keda.gulimall.coupon.entity.SmsCouponHistoryEntity;

import java.util.Map;

/**
 * 优惠券领取历史记录
 *
 * @author jmd
 * @email 928814697@qq.com
 * @date 2023-05-13 23:34:52
 */
public interface SmsCouponHistoryService extends IService<SmsCouponHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

