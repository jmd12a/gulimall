package com.keda.gulimall.goods.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keda.common.utils.ParamsUtils;
import com.keda.gulimall.goods.entity.SpuInfoEntity;
import com.keda.gulimall.goods.vo.Images;
import com.keda.gulimall.goods.vo.Skus;
import com.keda.gulimall.goods.vo.SpuSaveVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.keda.common.utils.PageUtils;
import com.keda.common.utils.Query;

import com.keda.gulimall.goods.dao.SkuInfoDao;
import com.keda.gulimall.goods.entity.SkuInfoEntity;
import com.keda.gulimall.goods.service.SkuInfoService;
import org.springframework.util.StringUtils;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageWithCondition(Map<String, Object> params) {

        String brandId = Optional.ofNullable(params.get("brandId")).map(o -> (String) o).filter(o->!"0".equals(o)).orElse("");
        String catelogId = Optional.ofNullable(params.get("catelogId")).map(o -> (String) o).filter(o->!"0".equals(o)).orElse("");
        String key = Optional.ofNullable(params.get("key")).map(o -> (String) o).orElse("");
        String min = Optional.ofNullable(params.get("min")).map(o -> (String) o).filter(o->!"0".equals(o)).orElse("");
        String max = Optional.ofNullable(params.get("max")).map(o -> (String) o).filter(o->!"0".equals(o)).orElse("");


        LambdaQueryWrapper<SkuInfoEntity> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(!StringUtils.isEmpty(brandId),SkuInfoEntity::getBrandId,brandId);
        queryWrapper.eq(!StringUtils.isEmpty(catelogId),SkuInfoEntity::getCatalogId,catelogId);
        queryWrapper.and(!StringUtils.isEmpty(key),w ->{
            w.like(SkuInfoEntity::getSkuId,key).or().like(SkuInfoEntity::getSkuName,key);
        });
        queryWrapper.ge(!StringUtils.isEmpty(min), SkuInfoEntity::getPrice, min);
        queryWrapper.le(!StringUtils.isEmpty(max), SkuInfoEntity::getPrice, max);



        Page page = ParamsUtils.acqPage(params);

        this.page(page,queryWrapper);


        return new PageUtils(page);
    }


}