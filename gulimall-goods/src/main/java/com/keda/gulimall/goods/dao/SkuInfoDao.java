package com.keda.gulimall.goods.dao;

import com.keda.gulimall.goods.common.ProductEsModel;
import com.keda.gulimall.goods.entity.SkuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sku信息
 * 
 * @author jmd
 * @email 928814697@qq.com
 * @date 2023-05-13 22:41:21
 */
@Mapper
public interface SkuInfoDao extends BaseMapper<SkuInfoEntity> {

    List<ProductEsModel> selectSkuInfoForProductEsModel(@Param("spuId") Long spuId);
}
