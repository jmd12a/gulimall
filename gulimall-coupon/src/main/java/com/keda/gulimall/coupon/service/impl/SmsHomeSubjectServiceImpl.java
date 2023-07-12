package com.keda.gulimall.coupon.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.keda.common.utils.PageUtils;
import com.keda.common.utils.Query;

import com.keda.gulimall.coupon.dao.SmsHomeSubjectDao;
import com.keda.gulimall.coupon.entity.SmsHomeSubjectEntity;
import com.keda.gulimall.coupon.service.SmsHomeSubjectService;


@Service("smsHomeSubjectService")
public class SmsHomeSubjectServiceImpl extends ServiceImpl<SmsHomeSubjectDao, SmsHomeSubjectEntity> implements SmsHomeSubjectService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SmsHomeSubjectEntity> page = this.page(
                new Query<SmsHomeSubjectEntity>().getPage(params),
                new QueryWrapper<SmsHomeSubjectEntity>()
        );

        return new PageUtils(page);
    }

}