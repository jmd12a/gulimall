package com.keda.gulimall.goods.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keda.common.Biz.Product;
import com.keda.common.utils.ParamsUtils;
import com.keda.gulimall.goods.dao.*;
import com.keda.gulimall.goods.entity.*;
import com.keda.gulimall.goods.service.ProductAttrValueService;
import com.keda.gulimall.goods.vo.AttrVo;
import org.bouncycastle.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.keda.common.utils.PageUtils;
import com.keda.common.utils.Query;

import com.keda.gulimall.goods.service.AttrService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Resource
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Resource
    private CategoryDao categoryDao;

    @Resource
    private AttrGroupDao attrGroupDao;

    @Resource
    private ProductAttrValueDao attrValueDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryBasePage(Map<String, Object> params, Long cateLogId, String attrType) {
        Page page = new ParamsUtils<AttrEntity>().getPage(params);

        String key = (String) params.get("key");

        Integer type = -1;
        if ("base".equals(attrType)){
            type = 1;
        }else {
            type = 0;
        }
        // 如果Id不为空，查cateLogId等于这个id的
        if (cateLogId != null && cateLogId != 0){
            if (!StringUtils.isEmpty(key)){ // 如果key不为空，查询id等于key或者name包含key的attr
                baseMapper.selectPage(page, new LambdaQueryWrapper<AttrEntity>()
                        .eq(AttrEntity::getCatelogId,cateLogId)
                        .eq(AttrEntity::getAttrType,type)
                        .and((obj)->{
                                obj.eq(AttrEntity::getAttrId,key)
                                        .or().like(AttrEntity::getAttrName,key);
                        }));
            }else baseMapper.selectPage(page,
                    new LambdaQueryWrapper<AttrEntity>()
                            .eq(AttrEntity::getCatelogId,cateLogId)
                            .eq(AttrEntity::getAttrType,type)); // null代表查询条件为空

        }else {
            if (!StringUtils.isEmpty(key)){ // 如果key不为空，查询id等于key或者name包含key的attr
                baseMapper.selectPage(page, new LambdaQueryWrapper<AttrEntity>()
                        .eq(AttrEntity::getAttrId,key)
                        .eq(AttrEntity::getAttrType,type)
                        .or().like(AttrEntity::getAttrName,key));
            }else baseMapper.selectPage(page, new LambdaQueryWrapper<AttrEntity>()
                        .eq(AttrEntity::getAttrType,type));
        }

        List<AttrEntity> records = page.getRecords();

        List<AttrVo> attrVos = records.stream().map(record -> {
            Long catelogId = record.getCatelogId();
            AttrVo attrVo = new AttrVo();
            BeanUtils.copyProperties(record, attrVo);

            attrVo.setCatelogName(categoryDao.selectById(catelogId).getName());

            if ("base".equals(attrType)){
                List<AttrAttrgroupRelationEntity> relationEntities = attrAttrgroupRelationDao.selectList(
                        new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                                .eq(AttrAttrgroupRelationEntity::getAttrId, record.getAttrId())
                                .select(AttrAttrgroupRelationEntity::getAttrGroupId));

                if (relationEntities.size()>0){
                    List<String> groupNames = relationEntities.stream().map(group -> {
                        Long groupId = group.getAttrGroupId();
                        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(groupId);
                        return attrGroupEntity.getAttrGroupName();
                    }).collect(Collectors.toList());



                    StringBuffer groupNameBuffer = new StringBuffer();

                    for (int i = 0; i < groupNames.size(); i++) {
                        groupNameBuffer.append(groupNames.get(i)).append("/");

                    }

                    if (groupNameBuffer.length() > 0){
                        String groupName = groupNameBuffer.toString().substring(0, groupNameBuffer.length() - 1);
                        attrVo.setGroupName(groupName);
                    }
                }

            }

            return attrVo;
        }).collect(Collectors.toList());

        page.setRecords(attrVos);


        return new PageUtils(page); // 这里page已经交给baseMapper的select方法处理了，引用指向的地址中已经存储了记录。无需返回值接收
    }

    @Override
    @Transactional
    public void saveDetail(AttrVo attrVo) {
        // 保存的同时更新和Attr有关系的表
        AttrEntity attrEntity = new AttrEntity(); // 与数据库交互的attrEntity

        AttrAttrgroupRelationEntity relation = new AttrAttrgroupRelationEntity();

        BeanUtils.copyProperties(attrVo,attrEntity);

        baseMapper.insert(attrEntity); // 执行完插入操作后，attrEntity对象的id和数据库中的id是同步的
        if (attrVo.getAttrGroupId() != null){
            relation.setAttrId(attrEntity.getAttrId());
            relation.setAttrGroupId(attrVo.getAttrGroupId());
            attrAttrgroupRelationDao.insert(relation);
        }

    }

    @Override
    public AttrVo getInfoById(Long attrId) {
        AttrEntity attrEntity = baseMapper.selectById(attrId);

        ArrayList<Long> path = new ArrayList<>();
        getCateLogPath(attrEntity.getCatelogId(), path);
        AttrVo attrVo = new AttrVo();
        BeanUtils.copyProperties(attrEntity,attrVo);
        Collections.reverse(path);
        attrVo.setCatelogPath(path);

        if (attrVo.getAttrType() == Product.AttrTypeEnum.ATTR_TYPE_BASE.getCode()){
            AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationDao.selectOne(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                    .eq(AttrAttrgroupRelationEntity::getAttrId, attrId)
                    .select(AttrAttrgroupRelationEntity::getAttrGroupId));
            if (relationEntity != null){
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
                attrVo.setGroupName(attrGroupEntity.getAttrGroupName());
            }
        }

        return attrVo;
    }

    @Override
    @Transactional
    public void updateDetail(AttrVo attrVo) {
        AttrEntity attr = new AttrEntity();
        BeanUtils.copyProperties(attrVo,attr);
        this.baseMapper.updateById(attr);

        AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationDao.selectOne(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                .eq(AttrAttrgroupRelationEntity::getAttrId, attr.getAttrId())
                );
        if (relationEntity == null){
            if (attrVo.getAttrGroupId() != null){
                AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();

                attrAttrgroupRelationEntity.setAttrGroupId(attrVo.getAttrGroupId());
                attrAttrgroupRelationEntity.setAttrId(attr.getAttrId());
                attrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);
            }

        }else if (attrVo.getAttrGroupId() != relationEntity.getAttrGroupId()){
            relationEntity.setAttrGroupId(attrVo.getAttrGroupId());
            attrAttrgroupRelationDao.updateById(relationEntity);
        }

    }

    @Override
    public boolean updateAttrValues(Long spuId, List<ProductAttrValueEntity> attrValues) {

        attrValues.forEach(attrValue -> {

            attrValue.setSpuId(spuId);
            attrValueDao.update(attrValue,new UpdateWrapper<ProductAttrValueEntity>()
                    .eq("spu_id",spuId)
                    .eq("attr_id",attrValue.getAttrId())
                    .set("attr_value",attrValue.getAttrValue())
                    .set("quick_show",attrValue.getQuickShow()));
        });
        return true;
    }

    private void getCateLogPath(Long categoryId, ArrayList<Long> groupEntities) {

        groupEntities.add(categoryId); // 先把categoryId添加到数组中

        CategoryEntity categoryEntity = categoryDao.selectById(categoryId);

        if (categoryEntity.getParentCid().intValue() != 0){ // 在查出来的categoryEntity的父类id不等于0的情况下进行递归，等于0时，递归结束
            getCateLogPath(categoryEntity.getParentCid(),groupEntities);
        }
    }


}