package com.keda.gulimall.goods.dao;

import com.keda.gulimall.goods.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author jmd
 * @email 928814697@qq.com
 * @date 2023-05-13 22:41:21
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
