package com.keda.gulimall.goods.service.impl;

import com.keda.common.utils.PageUtils;
import com.keda.common.utils.Query;
import com.keda.gulimall.goods.dao.AttrGroupDao;
import com.keda.gulimall.goods.entity.AttrEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.keda.gulimall.goods.dao.AttrAttrgroupRelationDao;
import com.keda.gulimall.goods.entity.AttrAttrgroupRelationEntity;
import com.keda.gulimall.goods.service.AttrAttrgroupRelationService;

import javax.annotation.Resource;


@Service("attrAttrgroupRelationService")
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrgroupRelationDao, AttrAttrgroupRelationEntity> implements AttrAttrgroupRelationService {

    @Resource
    private AttrGroupDao attrGroupDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrAttrgroupRelationEntity> page = this.page(
                new Query<AttrAttrgroupRelationEntity>().getPage(params),
                new QueryWrapper<AttrAttrgroupRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<AttrEntity> queryAttrByAttrGroupId(Integer attrGroupId) {


        return attrGroupDao.selectAttrByAttrGroupId(attrGroupId);
    }

}