package com.keda.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.keda.common.utils.PageUtils;
import com.keda.gulimall.ware.vo.CompletedVo;
import com.keda.gulimall.ware.entity.WmsPurchaseEntity;
import com.keda.gulimall.ware.vo.MergeVo;
import com.keda.gulimall.ware.vo.ReceiveVo;

import java.util.Map;

/**
 * 采购信息
 *
 * @author jmd
 * @email 928814697@qq.com
 * @date 2023-05-13 23:20:21
 */
public interface WmsPurchaseService extends IService<WmsPurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void merge(MergeVo merge);

    void receivePurchase(ReceiveVo receiveVo);

    void completedPurchase(CompletedVo completedVo);

}

