package com.keda.gulimall.goods.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.keda.common.utils.PageUtils;
import com.keda.gulimall.goods.entity.AttrEntity;
import com.keda.gulimall.goods.entity.ProductAttrValueEntity;
import com.keda.gulimall.goods.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author jmd
 * @email 928814697@qq.com
 * @date 2023-05-13 22:41:21
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryBasePage(Map<String, Object> params, Long cateLogId, String attrType);

    void saveDetail(AttrVo attrVo);

    AttrVo getInfoById(Long attrId);

    void updateDetail(AttrVo attr);

    boolean updateAttrValues(Long spuId, List<ProductAttrValueEntity> attrValues);
}

