package com.keda.gulimall.goods.dao;

import com.keda.gulimall.goods.common.ProductEsModel;
import com.keda.gulimall.goods.entity.SpuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * spu信息
 * 
 * @author jmd
 * @email 928814697@qq.com
 * @date 2023-05-13 22:41:20
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {

    ProductEsModel selectSpuInfoForProductEsModel(@Param("spuId") Long spuId);
}
