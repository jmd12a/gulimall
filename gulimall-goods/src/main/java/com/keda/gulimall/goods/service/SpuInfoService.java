package com.keda.gulimall.goods.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.keda.common.utils.PageUtils;
import com.keda.gulimall.goods.entity.SpuInfoEntity;

import java.util.Map;

/**
 * spu信息
 *
 * @author jmd
 * @email 928814697@qq.com
 * @date 2023-05-13 22:41:20
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

