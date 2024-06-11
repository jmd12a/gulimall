package com.keda.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.keda.common.Biz.WareConstant;
import com.keda.gulimall.fegin.feginClients.PmsSkuInfoClient;
import com.keda.gulimall.ware.common.PurchaseDetailEnum;
import com.keda.gulimall.ware.common.PurchaseEnum;
import com.keda.gulimall.ware.dao.WmsWareInfoDao;
import com.keda.gulimall.ware.dao.WmsWareSkuDao;
import com.keda.gulimall.ware.entity.WmsWareSkuEntity;
import com.keda.gulimall.ware.vo.CompletedVo;
import com.keda.gulimall.ware.dao.WmsPurchaseDetailDao;
import com.keda.gulimall.ware.entity.WmsPurchaseDetailEntity;
import com.keda.gulimall.ware.vo.MergeVo;
import com.keda.gulimall.ware.vo.ReceiveVo;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.keda.common.utils.PageUtils;
import com.keda.common.utils.Query;

import com.keda.gulimall.ware.dao.WmsPurchaseDao;
import com.keda.gulimall.ware.entity.WmsPurchaseEntity;
import com.keda.gulimall.ware.service.WmsPurchaseService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("wmsPurchaseService")
public class WmsPurchaseServiceImpl extends ServiceImpl<WmsPurchaseDao, WmsPurchaseEntity> implements WmsPurchaseService {

    @Resource
    private WmsPurchaseDetailDao purchaseDetailDao;

    @Resource
    private WmsPurchaseDao wmsPurchaseDao;

    @Resource
    private WmsWareSkuDao wmsWareSkuDao;

    @Resource
    private PmsSkuInfoClient pmsSkuInfoClient;

    @Resource
    private WmsWareInfoDao wmsWareInfoDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WmsPurchaseEntity> page = this.page(
                new Query<WmsPurchaseEntity>().getPage(params),
                new QueryWrapper<WmsPurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void merge(MergeVo merge) {

        List<WmsPurchaseDetailEntity> purchaseDetails =
                purchaseDetailDao.selectList(new QueryWrapper<WmsPurchaseDetailEntity>().in("id", merge.getItems()));

        // 判断是否有没有所属仓库的
        if (purchaseDetails.stream().anyMatch(p -> p.getWareId() == null)) {
            throw new RuntimeException("存在没有所属仓库的采购需求，无法合并");
        }

        // 判断所属仓库是否只有一个
        boolean b = purchaseDetails.stream().map(WmsPurchaseDetailEntity::getWareId).distinct().count() != 1;

        if (b){
            throw new RuntimeException("传入的采购需求所属仓库不同，无法合并");
        }

        Long purchaseId = merge.getPurchaseId();
        Long wareId = purchaseDetails.get(0).getWareId();

        if (purchaseId == null){
            // 如果没有传入采购单id就新建
            WmsPurchaseEntity entity = new WmsPurchaseEntity();
            entity.setStatus(WareConstant.PurchaseStatusEnum.CREATE.getCode());
            entity.setPriority(1);
            entity.setWareId(wareId);

            this.save(entity);

            purchaseId = entity.getId();
        }else {
            // 有的话进行判断
            WmsPurchaseEntity purchase = this.getById(purchaseId);

            if (purchase == null){
                throw new RuntimeException("传入的采购单Id不存在！");
            }
            if (purchase.getWareId().longValue() != wareId){
                throw new RuntimeException("采购需求所属仓库和要合并到的采购单所属仓库不同，无法合并");
            }

            if (! (purchase.getStatus() == PurchaseEnum.NEW.getCode().intValue()
                    || purchase.getStatus() == PurchaseEnum.ASSIGNED.getCode().intValue())) {
                throw new RuntimeException("要合并到的采购单已被领取，无法合并");
            }
        }

        Long pId  = purchaseId;

        // 设置采购单Id后保存
        purchaseDetails.forEach(p -> {
            p.setPurchaseId(pId);
            p.setStatus(1);
            purchaseDetailDao.updateById(p);
        });

    }

    @Override
    @Transactional
    public void receivePurchase(ReceiveVo receiveVo) {

      /*  // 判断参数是否有问题
        if (receiveVo.getUserId() == null || CollectionUtils.isEmpty(receiveVo.getPurchaseIds())){
            throw new RuntimeException("采购人员Id为空，或采购单id列表为空");
        }*/

        List<WmsPurchaseEntity> purchaseEntities = this.listByIds(receiveVo.getPurchaseIds());

        if (CollectionUtils.isEmpty(purchaseEntities))
            throw new RuntimeException("传入的id没有对应的采购单");


        // 过滤不是所属用户的采购单和状态不是新建或已分配的采购单
        List<Long> purIds = purchaseEntities.stream()
                .filter(p -> p.getAssigneeId() == receiveVo.getUserId() && (p.getStatus() == 0 || p.getStatus() == 1))
                .map(WmsPurchaseEntity::getId).collect(Collectors.toList());

        // 更新采购单状态
        UpdateWrapper<WmsPurchaseEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id",purIds);
        updateWrapper.set("status",2);
        this.update(updateWrapper);

        // 更新采购下的采购详情的状态
        UpdateWrapper<WmsPurchaseDetailEntity> updateWrapperForDetail = new UpdateWrapper<>();

        updateWrapperForDetail.in("purchase_id",purIds);
        updateWrapperForDetail.set("status",2);
        purchaseDetailDao.update(null,updateWrapperForDetail);
    }

    @Override
    @Transactional
    public void completedPurchase(CompletedVo completedVo) {
        Long purchaseId = completedVo.getPurchaseId();

        // 改变采购项的状态

        List<Map<String, String>> items = completedVo.getItems();

        boolean isSuccess = true;

        ArrayList<Long> sucPurDetailIds = new ArrayList<>();

        for (Map<String, String> item : items) {

            Long detailId = Long.valueOf(item.get("id"));

            int status = Integer.parseInt(item.get("status"));

            String reason = item.get("reason");

            WmsPurchaseDetailEntity detailEntity = new WmsPurchaseDetailEntity();

            if (status == PurchaseDetailEnum.GO_WRONG.getCode()){

                isSuccess = false;
                detailEntity.setReason(reason);
                detailEntity.setStatus(status);

            }else {
                //  采购成功的设值原因为空字符串

                detailEntity.setReason("");
                detailEntity.setStatus(PurchaseEnum.COMPLETED.getCode());
                // 采购成功的将id放到列表中，后续将修改到库存表
                sucPurDetailIds.add(detailId);

            }

            detailEntity.setId(detailId);
            detailEntity.setStatus(status);

            purchaseDetailDao.updateById(detailEntity);
        }



        // 改变采购单的状态

        WmsPurchaseEntity purchaseEntity = new WmsPurchaseEntity();

        purchaseEntity.setId(purchaseId);

        if (isSuccess)
            purchaseEntity.setStatus(PurchaseEnum.COMPLETED.getCode());
        else purchaseEntity.setStatus(PurchaseEnum.ABNORMAL.getCode());

        this.updateById(purchaseEntity);

        List<WmsPurchaseDetailEntity> purchaseDetailEntities = purchaseDetailDao.selectBatchIds(sucPurDetailIds);


        // 一个一个查之前，先排序，在记录skuId和wareId，然后，每次查之前判断上次是否查过,减少查询数据库次数

       /* long skuId = 0l;
        long wareId = 0l;

        purchaseDetailEntities.sort(new Comparator<WmsPurchaseDetailEntity>() {
            @Override
            public int compare(WmsPurchaseDetailEntity o1, WmsPurchaseDetailEntity o2) {
                Long l = o1.getWareId() - o2.getWareId();
                Long l1 = o1.getSkuId() - o2.getSkuId();

                if (l==0l) return l1.intValue();
                else return l.intValue();

            }
        });
        for (int i = 0; i < purchaseDetailEntities.size(); i++) {
            WmsPurchaseDetailEntity detail = purchaseDetailEntities.get(i);

            // 在判断之前，先判断下之前是否判断过，因为判断的因素，就是skuId和wareId，所以看这俩有一个和之前不相等，就对比过
            if (detail.getSkuId() != skuId || detail.getWareId() != wareId){
                // 判断这个采购详情，是否有对应的仓库和商品号
                Integer count = wmsWareSkuDao.selectCount(new LambdaQueryWrapper<WmsWareSkuEntity>()
                        .eq(WmsWareSkuEntity::getSkuId, detail.getSkuId())
                        .eq(WmsWareSkuEntity::getWareId, detail.getWareId()));

                if (count == 0){
                    WmsWareSkuEntity wmsWareSkuEntity = new WmsWareSkuEntity();
                    wmsWareSkuEntity.setWareId(detail.getWareId());
                    wmsWareSkuEntity.setSkuId(detail.getSkuId());
                    wmsWareSkuEntity.setStock(0);
                    wmsWareSkuEntity.setSkuName(String.valueOf(pmsSkuInfoClient.queryName(detail.getSkuId()).get("name")));

                    wmsWareSkuDao.insert(wmsWareSkuEntity);
                }

                skuId = detail.getSkuId();
                wareId = detail.getWareId();
            }

        }*/

        purchaseDetailEntities.forEach( detail ->{
            Integer count = wmsWareSkuDao.selectCount(new LambdaQueryWrapper<WmsWareSkuEntity>()
                    .eq(WmsWareSkuEntity::getSkuId, detail.getSkuId())
                    .eq(WmsWareSkuEntity::getWareId, detail.getWareId()));

            if (count == 0){
                WmsWareSkuEntity wmsWareSkuEntity = new WmsWareSkuEntity();
                wmsWareSkuEntity.setWareId(detail.getWareId());
                wmsWareSkuEntity.setSkuId(detail.getSkuId());
                wmsWareSkuEntity.setStock(0);
                wmsWareSkuEntity.setSkuName(String.valueOf(pmsSkuInfoClient.queryName(detail.getSkuId()).get("name")));

                wmsWareSkuDao.insert(wmsWareSkuEntity);
            }


        });

        // 增加采购成功的库存, 直接使用sql语句，update的多表更新
        wmsPurchaseDao.addRepertoryByPurDetailId(sucPurDetailIds);

    }

}