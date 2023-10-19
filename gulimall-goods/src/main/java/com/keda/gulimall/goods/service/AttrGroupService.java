package com.keda.gulimall.goods.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.keda.common.utils.PageUtils;
import com.keda.gulimall.goods.entity.AttrGroupEntity;

import java.util.ArrayList;
import java.util.Map;

/**
 * 属性分组
 *
 * @author jmd
 * @email 928814697@qq.com
 * @date 2023-05-13 22:41:21
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageWithCategoryId(Map<String, Object> params, Long id);

    public void getCateLogPath(Long categoryId, ArrayList<Long> groupEntities);

    Page getAttrWithoutRelation(Long attrgroupId, String key, Map<String, Object> map);
}

