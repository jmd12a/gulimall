package com.keda.gulimall.goods.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keda.common.utils.ParamsUtils;
import com.keda.gulimall.goods.dao.AttrAttrgroupRelationDao;
import com.keda.gulimall.goods.dao.AttrDao;
import com.keda.gulimall.goods.dao.CategoryDao;
import com.keda.gulimall.goods.entity.AttrAttrgroupRelationEntity;
import com.keda.gulimall.goods.entity.AttrEntity;
import com.keda.gulimall.goods.entity.CategoryEntity;
import com.keda.gulimall.goods.vo.AttrGroupVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.keda.common.utils.PageUtils;
import com.keda.common.utils.Query;

import com.keda.gulimall.goods.dao.AttrGroupDao;
import com.keda.gulimall.goods.entity.AttrGroupEntity;
import com.keda.gulimall.goods.service.AttrGroupService;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Resource
    private CategoryDao categoryDao;


    @Resource
    private AttrGroupDao attrGroupDao;


    @Resource
    private AttrAttrgroupRelationDao relationDao;

    @Resource
    private AttrDao attrDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageWithCategoryId(Map<String, Object> params, Long categoryId) {

        String key = (String) params.get("key");
        LambdaQueryWrapper<AttrGroupEntity> wrapper;
        if (categoryId == 0){
            if (StringUtils.isEmpty(key)){
                wrapper = new LambdaQueryWrapper<AttrGroupEntity>();
            }else {
                wrapper = new LambdaQueryWrapper<AttrGroupEntity>()
                        .eq(AttrGroupEntity::getAttrGroupId, key)
                        .or().like(AttrGroupEntity::getAttrGroupName,key);
            }

        }else {
            wrapper = new LambdaQueryWrapper<AttrGroupEntity>()
                    .eq(AttrGroupEntity::getCatelogId, categoryId)
                    .and(!StringUtils.isEmpty(key), (obj) -> {
                        obj.eq(AttrGroupEntity::getAttrGroupId, key).or().like(AttrGroupEntity::getAttrGroupName,key);
                    });
        }

        Page<AttrGroupEntity> page = this.page(new ParamsUtils<AttrGroupEntity>().getPage(params), wrapper);

        // 给结果设置catelogPath
        /*List<AttrGroupEntity> records = page.getRecords();
        List<AttrGroupEntity> recordsWithCatelogPath = records.stream().map(record -> {

            ArrayList<Long> groupEntities = new ArrayList<>();

            this.getCateLogPath(categoryId, groupEntities);

            // 此时groupEntities的地址给了getCateLogPath方法以后，这个列表的内部已经添加了categoryId及其上层的id，但子id在前，父id在后
            Collections.reverse(groupEntities);

            record.setCatelogPath(groupEntities.toArray(new Long[groupEntities.size()]));
            return record;
        }).collect(Collectors.toList());

        page.setRecords(recordsWithCatelogPath);*/

        PageUtils pageUtils = new PageUtils(page);

        return pageUtils;
    }

    public void getCateLogPath(Long categoryId, ArrayList<Long> groupEntities) {

        groupEntities.add(categoryId); // 先把categoryId添加到数组中

        CategoryEntity categoryEntity = categoryDao.selectById(categoryId);

        if (categoryEntity.getParentCid().intValue() != 0){ // 在查出来的categoryEntity的父类id不等于0的情况下进行递归，等于0时，递归结束
            getCateLogPath(categoryEntity.getParentCid(),groupEntities);
        }
    }

    @Override
    public Page getAttrWithoutRelation(Long attrgroupId, String key, Map<String, Object> map) {


        return attrGroupDao.getAttrWithoutRelation(attrgroupId, key, new ParamsUtils<AttrEntity>().getPage(map));

    }

    @Override
    public List<AttrGroupVo> getAttrGroupWithAttr(String categoryId) {

        List<AttrGroupEntity> attrGroupEntities =
                attrGroupDao.selectList(new LambdaQueryWrapper<AttrGroupEntity>().eq(AttrGroupEntity::getCatelogId, categoryId));

        List<Long> groupIds = attrGroupEntities.stream().map(attrGroupEntity -> {
            return attrGroupEntity.getAttrGroupId();
        }).collect(Collectors.toList());

        List<AttrAttrgroupRelationEntity> relationEntities = relationDao.selectList(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                .in(AttrAttrgroupRelationEntity::getAttrGroupId, groupIds));

        List<Long> attrIds = relationEntities.stream().map(relation -> {
            return relation.getAttrId();
        }).collect(Collectors.toList());

        List<AttrEntity> attrEntities = attrDao.selectBatchIds(attrIds);

        HashMap<Long, Integer> idAndKeymap = new HashMap<>();
        for (int i = 0; i < attrEntities.size(); i++) {

            idAndKeymap.put(attrEntities.get(i).getAttrId(),i);
        }

        HashMap<Long, ArrayList<Long>> groupIdAndAttrIdsMap = new HashMap<>();

        relationEntities.forEach(relation ->{
            Long attrGroupId = relation.getAttrGroupId();
            ArrayList<Long> attrIdList = groupIdAndAttrIdsMap.getOrDefault(attrGroupId, new ArrayList<Long>());
            attrIdList.add(relation.getAttrId());

            groupIdAndAttrIdsMap.put(relation.getAttrGroupId(),attrIdList);

        });

        List<AttrGroupVo> groupVos = attrGroupEntities.stream().map(attrGroupEntity -> {
            AttrGroupVo attrGroupVo = new AttrGroupVo();
            BeanUtils.copyProperties(attrGroupEntity, attrGroupVo);
            attrGroupVo.setAttrs(new ArrayList<AttrEntity>());
            Long attrGroupId = attrGroupEntity.getAttrGroupId();

            ArrayList<Long> attrIdsOfThisGroup = groupIdAndAttrIdsMap.get(attrGroupEntity.getAttrGroupId());



            attrIdsOfThisGroup.forEach(id -> {

                attrGroupVo.getAttrs().add(attrEntities.get(idAndKeymap.get(id))) ;
            });

            return attrGroupVo;
        }).collect(Collectors.toList());

        return groupVos;
    }

}