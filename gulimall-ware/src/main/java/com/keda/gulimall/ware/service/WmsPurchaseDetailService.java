package com.keda.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.keda.common.utils.PageUtils;
import com.keda.gulimall.ware.entity.WmsPurchaseDetailEntity;

import java.util.Map;

/**
 * 
 *
 * @author jmd
 * @email 928814697@qq.com
 * @date 2023-05-13 23:20:21
 */
public interface WmsPurchaseDetailService extends IService<WmsPurchaseDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageWithCondition(Map<String, Object> params);

}

