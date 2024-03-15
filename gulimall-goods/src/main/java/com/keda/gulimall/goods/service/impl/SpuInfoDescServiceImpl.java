package com.keda.gulimall.goods.service.impl;

import com.keda.gulimall.goods.entity.SpuInfoEntity;
import com.keda.gulimall.goods.vo.Images;
import com.keda.gulimall.goods.vo.Skus;
import com.keda.gulimall.goods.vo.SpuSaveVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.keda.common.utils.PageUtils;
import com.keda.common.utils.Query;

import com.keda.gulimall.goods.dao.SpuInfoDescDao;
import com.keda.gulimall.goods.entity.SpuInfoDescEntity;
import com.keda.gulimall.goods.service.SpuInfoDescService;


@Service("spuInfoDescService")
public class SpuInfoDescServiceImpl extends ServiceImpl<SpuInfoDescDao, SpuInfoDescEntity> implements SpuInfoDescService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoDescEntity> page = this.page(
                new Query<SpuInfoDescEntity>().getPage(params),
                new QueryWrapper<SpuInfoDescEntity>()
        );

        return new PageUtils(page);
    }




}