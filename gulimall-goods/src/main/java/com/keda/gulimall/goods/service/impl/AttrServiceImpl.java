package com.keda.gulimall.goods.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keda.common.utils.ParamsUtils;
import com.keda.gulimall.goods.dao.AttrAttrgroupRelationDao;
import com.keda.gulimall.goods.entity.AttrAttrgroupRelationEntity;
import com.keda.gulimall.goods.vo.AttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.keda.common.utils.PageUtils;
import com.keda.common.utils.Query;

import com.keda.gulimall.goods.dao.AttrDao;
import com.keda.gulimall.goods.entity.AttrEntity;
import com.keda.gulimall.goods.service.AttrService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Resource
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryBasePage(Map<String, Object> params, Long cateLogId) {
        Page<AttrEntity> page = new ParamsUtils<AttrEntity>().getPage(params);

        String key = (String) params.get("key");

        // 如果Id不为空，查cateLogId等于这个id的
        if (cateLogId != null && cateLogId != 0){
            if (!StringUtils.isEmpty(key)){ // 如果key不为空，查询id等于key或者name包含key的attr
                baseMapper.selectPage(page, new LambdaQueryWrapper<AttrEntity>()
                        .eq(AttrEntity::getCatelogId,cateLogId)
                        .and((obj)->{
                                obj.eq(AttrEntity::getAttrId,key)
                                        .or().like(AttrEntity::getAttrName,key);
                        }));
            }else baseMapper.selectPage(page,
                    new LambdaQueryWrapper<AttrEntity>()
                            .eq(AttrEntity::getCatelogId,cateLogId)); // null代表查询条件为空

        }else {
            if (!StringUtils.isEmpty(key)){ // 如果key不为空，查询id等于key或者name包含key的attr
                baseMapper.selectPage(page, new LambdaQueryWrapper<AttrEntity>()
                        .eq(AttrEntity::getAttrId,key)
                        .or().like(AttrEntity::getAttrName,key));
            }else baseMapper.selectPage(page, null); // null代表查询条件为空, 查所有
        }


        return new PageUtils(page); // 这里page已经交给baseMapper的select方法处理了，引用指向的地址中已经存储了记录。无需返回值接收
    }

    @Override
    @Transactional
    public void saveDetail(AttrVo attrVo) {
        // 保存的同时更新和Attr有关系的表
        AttrEntity attrEntity = new AttrEntity(); // 与数据库交互的attrEntity

        AttrAttrgroupRelationEntity relation = new AttrAttrgroupRelationEntity();

        BeanUtils.copyProperties(attrVo,attrEntity);

        baseMapper.insert(attrEntity); // 执行玩插入操作后，attrEntity对象的id和数据库中的id是同步的

        relation.setAttrId(attrEntity.getAttrId());
        relation.setAttrGroupId(attrVo.getAttrGroupId());
        attrAttrgroupRelationDao.insert(relation);
    }

}