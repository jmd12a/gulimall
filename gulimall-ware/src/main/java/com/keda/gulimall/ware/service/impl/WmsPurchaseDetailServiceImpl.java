package com.keda.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keda.common.utils.ParamsUtils;
import org.aspectj.weaver.ast.Var;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.keda.common.utils.PageUtils;
import com.keda.common.utils.Query;

import com.keda.gulimall.ware.dao.WmsPurchaseDetailDao;
import com.keda.gulimall.ware.entity.WmsPurchaseDetailEntity;
import com.keda.gulimall.ware.service.WmsPurchaseDetailService;
import org.springframework.util.StringUtils;


@Service("wmsPurchaseDetailService")
public class WmsPurchaseDetailServiceImpl extends ServiceImpl<WmsPurchaseDetailDao, WmsPurchaseDetailEntity> implements WmsPurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WmsPurchaseDetailEntity> page = this.page(
                new Query<WmsPurchaseDetailEntity>().getPage(params),
                new QueryWrapper<WmsPurchaseDetailEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageWithCondition(Map<String, Object> params) {
        Page page = ParamsUtils.acqPage(params);

        String key = (String) params.get("key");

        String status = (String) params.get("status");

        String wareId = (String) params.get("wareId");

        QueryWrapper<WmsPurchaseDetailEntity> queryWrapper = new QueryWrapper<>();

        // queryWrapper.eq(!StringUtils.isEmpty(key),  "sku_name", key);
        queryWrapper.eq(!StringUtils.isEmpty(status),"status",status);
        queryWrapper.eq(!StringUtils.isEmpty(wareId),"ware_id",wareId);

        this.page(page,queryWrapper);

        return new PageUtils(page);
    }

}