package com.keda.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keda.common.utils.ParamsUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.keda.common.utils.PageUtils;
import com.keda.common.utils.Query;

import com.keda.gulimall.ware.dao.WmsWareSkuDao;
import com.keda.gulimall.ware.entity.WmsWareSkuEntity;
import com.keda.gulimall.ware.service.WmsWareSkuService;
import org.springframework.util.StringUtils;


@Service("wmsWareSkuService")
public class WmsWareSkuServiceImpl extends ServiceImpl<WmsWareSkuDao, WmsWareSkuEntity> implements WmsWareSkuService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WmsWareSkuEntity> page = this.page(
                new Query<WmsWareSkuEntity>().getPage(params),
                new QueryWrapper<WmsWareSkuEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageWithCondition(Map<String, Object> params) {

        Page page = ParamsUtils.acqPage(params);

        String skuId = (String) params.get("skuId");

        String wareId = (String) params.get("wareId");

        QueryWrapper<WmsWareSkuEntity> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq(!StringUtils.isEmpty(skuId),"sku_id",skuId);
        queryWrapper.eq(!StringUtils.isEmpty(wareId),"ware_id",wareId);

        this.page(page,queryWrapper);

        return new PageUtils(page);
    }

}