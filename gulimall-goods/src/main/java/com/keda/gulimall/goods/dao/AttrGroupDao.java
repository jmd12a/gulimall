package com.keda.gulimall.goods.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keda.gulimall.goods.entity.AttrEntity;
import com.keda.gulimall.goods.entity.AttrGroupEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性分组
 * 
 * @author jmd
 * @email 928814697@qq.com
 * @date 2023-05-13 22:41:21
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

    List<AttrEntity> selectAttrByAttrGroupId(@Param("attrGroupId") Integer attrGroupId);

    Page getAttrWithoutRelation(@Param("attrGroupId") Long attrgroupId,
                                @Param("key") String key,
                                Page<AttrEntity> page);

}
