package com.keda.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keda.common.utils.ParamsUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.keda.common.utils.PageUtils;
import com.keda.common.utils.Query;

import com.keda.gulimall.ware.dao.WmsWareInfoDao;
import com.keda.gulimall.ware.entity.WmsWareInfoEntity;
import com.keda.gulimall.ware.service.WmsWareInfoService;
import org.springframework.util.StringUtils;


@Service("wmsWareInfoService")
public class WmsWareInfoServiceImpl extends ServiceImpl<WmsWareInfoDao, WmsWareInfoEntity> implements WmsWareInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WmsWareInfoEntity> page = this.page(
                new Query<WmsWareInfoEntity>().getPage(params),
                new QueryWrapper<WmsWareInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageWithCondition(Map<String, Object> params) {

        Page page = ParamsUtils.acqPage(params);

        String key = (String) params.get("key");

        LambdaQueryWrapper<WmsWareInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(key)){

            queryWrapper.like(WmsWareInfoEntity::getId,key)
                    .or().like(WmsWareInfoEntity::getName,key)
                    .or().like(WmsWareInfoEntity::getAddress,key)
                    .or().like(WmsWareInfoEntity::getAreacode,key);
        }

        this.page(page,queryWrapper);

        return new PageUtils(page);

    }

}