package com.keda.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.keda.common.to.SkuReductionTo;
import com.keda.common.utils.PageUtils;
import com.keda.gulimall.coupon.entity.SmsSkuFullReductionEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品满减信息
 *
 * @author jmd
 * @email 928814697@qq.com
 * @date 2023-05-13 23:34:52
 */
public interface SmsSkuFullReductionService extends IService<SmsSkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuReductionToBatch(List<SkuReductionTo> skuReductionTos);

}

