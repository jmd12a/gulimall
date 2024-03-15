package com.keda.gulimall.coupon.service.impl;

import com.keda.common.to.MemberPrice;
import com.keda.common.to.SkuReductionTo;
import com.keda.gulimall.coupon.entity.SmsMemberPriceEntity;
import com.keda.gulimall.coupon.entity.SmsSkuLadderEntity;
import com.keda.gulimall.coupon.service.SmsMemberPriceService;
import com.keda.gulimall.coupon.service.SmsSkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.keda.common.utils.PageUtils;
import com.keda.common.utils.Query;

import com.keda.gulimall.coupon.dao.SmsSkuFullReductionDao;
import com.keda.gulimall.coupon.entity.SmsSkuFullReductionEntity;
import com.keda.gulimall.coupon.service.SmsSkuFullReductionService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("smsSkuFullReductionService")
public class SmsSkuFullReductionServiceImpl extends ServiceImpl<SmsSkuFullReductionDao, SmsSkuFullReductionEntity> implements SmsSkuFullReductionService {

    @Resource
    private SmsMemberPriceService smsMemberPriceService;

    @Resource
    private SmsSkuLadderService smsSkuLadderService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SmsSkuFullReductionEntity> page = this.page(
                new Query<SmsSkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SmsSkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }


    @Override
    @Transactional
    public void saveSkuReductionToBatch(List<SkuReductionTo> skuReductionTos) {
        ArrayList<SmsSkuLadderEntity> ladderEntities = new ArrayList<>();
        ArrayList<SmsSkuFullReductionEntity> smsSkuFullReductionEntities = new ArrayList<>();
        ArrayList<SmsMemberPriceEntity> smsMemberPriceEntities = new ArrayList<>();


        for (SkuReductionTo skuReductionTo : skuReductionTos) {

            if (skuReductionTo.getFullCount() >= 1 && // 最少满1件
                    skuReductionTo.getDiscount().compareTo(new BigDecimal(1)) == -1 &&  // 打折要比1小
                    skuReductionTo.getDiscount().compareTo(new BigDecimal(0)) == 1 ){ // 打折比0大
                // 封装打折信息
                SmsSkuLadderEntity smsSkuLadderEntity = new SmsSkuLadderEntity();
                smsSkuLadderEntity.setSkuId(skuReductionTo.getSkuId());
                smsSkuLadderEntity.setAddOther(skuReductionTo.getCountStatus());
                smsSkuLadderEntity.setFullCount(skuReductionTo.getFullCount());
                smsSkuLadderEntity.setDiscount(skuReductionTo.getDiscount());
                smsSkuLadderEntity.setAddOther(1);

                ladderEntities.add(smsSkuLadderEntity);
            }



            // 封装满减信息

            SmsSkuFullReductionEntity smsSkuFullReductionEntity = new SmsSkuFullReductionEntity();
            BeanUtils.copyProperties(skuReductionTo,smsSkuFullReductionEntity);
            // 满的金额大于0，减的金额大于0，且满的金额大于减的金额，才有意义
            if (smsSkuFullReductionEntity.getFullPrice().compareTo(new BigDecimal("0")) == 1
                    && smsSkuFullReductionEntity.getReducePrice().compareTo(new BigDecimal(0)) == 1
                    && smsSkuFullReductionEntity.getFullPrice().compareTo(smsSkuFullReductionEntity.getReducePrice()) ==1){
                smsSkuFullReductionEntity.setAddOther(1);
                smsSkuFullReductionEntities.add(smsSkuFullReductionEntity);
            }





            // 封装会员价格
            if (skuReductionTo.getMemberPrice() != null && skuReductionTo.getMemberPrice().size() > 0){

                List<SmsMemberPriceEntity> memberPriceEntities = skuReductionTo.getMemberPrice().stream()
                        .filter(memberPrice -> {
                            // 会员价格大于0才有意义
                            return memberPrice.getPrice().compareTo(new BigDecimal("0")) == 1;
                        })
                        .map(memberPrice -> {
                    SmsMemberPriceEntity smsMemberPriceEntity = new SmsMemberPriceEntity();
                    smsMemberPriceEntity.setMemberPrice(memberPrice.getPrice());
                    smsMemberPriceEntity.setSkuId(skuReductionTo.getSkuId());
                    smsMemberPriceEntity.setMemberLevelName(memberPrice.getName());
                    smsMemberPriceEntity.setMemberLevelId(memberPrice.getId());

                    return smsMemberPriceEntity;
                }).collect(Collectors.toList());

                smsMemberPriceEntities.addAll(memberPriceEntities);
            }
        }


        if (ladderEntities.size() > 0){
            smsSkuLadderService.saveBatch(ladderEntities);
        }

        if (smsSkuFullReductionEntities.size() > 0){
            this.saveBatch(smsSkuFullReductionEntities);
        }

        if (smsMemberPriceEntities.size()>0){
            smsMemberPriceService.saveBatch(smsMemberPriceEntities);
        }
    }

}