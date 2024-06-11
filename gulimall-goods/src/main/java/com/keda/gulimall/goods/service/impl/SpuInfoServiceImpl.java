package com.keda.gulimall.goods.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keda.common.to.BoundsTo;
import com.keda.common.to.SkuReductionTo;
import com.keda.common.utils.ParamsUtils;
import com.keda.common.utils.R;
import com.keda.gulimall.fegin.feginClients.BoundsClients;
import com.keda.gulimall.goods.common.ElasticsearchOption;
import com.keda.gulimall.goods.common.ProductEsModel;
import com.keda.gulimall.goods.dao.*;
import com.keda.gulimall.goods.entity.*;
import com.keda.gulimall.goods.service.SpuImagesService;
import com.keda.gulimall.goods.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;

import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.keda.common.utils.PageUtils;
import com.keda.common.utils.Query;

import com.keda.gulimall.goods.service.SpuInfoService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;


@Service("spuInfoService")
@Slf4j
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {



    @Resource
    private SpuInfoDescDao spuInfoDescDao;

    @Resource
    private SpuImagesDao spuImagesDao;

    @Resource
    private AttrDao attrDao;

    @Resource
    private ProductAttrValueDao productAttrValueDao;

    @Resource
    private SkuInfoDao skuInfoDao;

    @Resource
    private SkuImagesDao skuImagesDao;

    @Resource
    private SkuSaleAttrValueDao skuSaleAttrValueDao;

    @Resource
    private BoundsClients boundsClients;

    @Resource
    private RestHighLevelClient esClient;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }


    @Override
    @Transactional
    // TODO 分布式事物的处理
    public void saveDetail(SpuSaveVo spuSaveVo) {

        // 保存spu的基本休息
        SpuInfoEntity spuInfo = new SpuInfoEntity();

        BeanUtils.copyProperties(spuSaveVo,spuInfo);

        this.save(spuInfo);

        Long spuInfoId = spuInfo.getId();

        // 保存spu的详情信息

        List<String> decript = spuSaveVo.getDecript();

        StringBuilder decriptStringBuilder = new StringBuilder();

        // 拼接详情信息
        decript.forEach(d ->{
            decriptStringBuilder.append(d+";");
        });

        String decripts = decriptStringBuilder.toString().substring(0, decriptStringBuilder.length() - 1);

        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();

        spuInfoDescEntity.setSpuId(spuInfoId);
        spuInfoDescEntity.setDecript(decripts);
        spuInfoDescDao.insert(spuInfoDescEntity);

        // 保存spu的图片信息
        List<String> images = spuSaveVo.getImages();

        images.stream().forEach(im ->{
            SpuImagesEntity spuImages = new SpuImagesEntity();
            spuImages.setSpuId(spuInfoId);
            spuImages.setImgUrl(im);
            // spuImages.setDefaultImg(1);
            spuImagesDao.insert(spuImages);
        });

        // 保存spu的属性信息

        List<BaseAttrs> baseAttrs = spuSaveVo.getBaseAttrs();

        List<Long> attrIds = baseAttrs.stream().map(BaseAttrs::getAttrId).collect(Collectors.toList());
        List<AttrEntity> attrEntities = attrDao.selectBatchIds(attrIds);

        HashMap<Long, String> attrIdNameHash = new HashMap<>();

        attrEntities.forEach(attrEntity -> {
            attrIdNameHash.put(attrEntity.getAttrId(),attrEntity.getAttrName());
        });

        baseAttrs.forEach(attr ->{
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();

            productAttrValueEntity.setAttrId(attr.getAttrId());
            productAttrValueEntity.setAttrName(attrIdNameHash.get(attr.getAttrId()));
            productAttrValueEntity.setAttrValue(attr.getAttrValues());
            productAttrValueEntity.setQuickShow(attr.getShowDesc());
            productAttrValueEntity.setSpuId(spuInfoId);

            productAttrValueDao.insert(productAttrValueEntity);

        });

        // 保存sku的信息
        List<Skus> skus = spuSaveVo.getSkus();
        ArrayList<BoundsTo> boundsTos = new ArrayList<>();
        ArrayList<SkuReductionTo> skuReductionTos = new ArrayList<>();


        skus.forEach(sku ->{
          //     保存sku的基本信息
            SkuInfoEntity skuInfoEntity = new SkuInfoEntity();

            skuInfoEntity.setSpuId(spuInfoId);
            skuInfoEntity.setBrandId(spuSaveVo.getBrandId());
            skuInfoEntity.setCatalogId(spuSaveVo.getCatalogId());

            //名称和标题信息
            skuInfoEntity.setSkuName(sku.getSkuName());
            skuInfoEntity.setSkuTitle(sku.getSkuTitle());
            skuInfoEntity.setSkuSubtitle(sku.getSkuSubtitle());

            // 拼接详情信息
            List<String> descar = sku.getDescar();
            String desc = descar.stream().collect(Collectors.joining(";"));
            skuInfoEntity.setSkuDesc(desc);

            // 价格信息
            skuInfoEntity.setPrice(sku.getPrice());
            skuInfoEntity.setSaleCount(0L); // 销量 添加商品时默认为0
            try {
                sku.getImages().forEach(image -> {
                    if (image.getDefaultImg()==1) {
                        skuInfoEntity.setSkuDefaultImg(image.getImgUrl());
                    }

                    throw new Error("找到默认的图像");
                });
            } catch (Error e){

                System.out.println("结束循环");
            }

            skuInfoDao.insert(skuInfoEntity);

          // 保存sku的图像信息
            Long skuId = skuInfoEntity.getSkuId();
            // filter过滤出url不为空的再保存
            sku.getImages().stream().filter(image -> !StringUtils.isEmpty(image.getImgUrl()))
                                    .forEach(image -> {
                SkuImagesEntity skuImage = new SkuImagesEntity();
                skuImage.setImgUrl(image.getImgUrl());
                skuImage.setDefaultImg(image.getDefaultImg());
                skuImage.setSkuId(skuId);

                skuImagesDao.insert(skuImage);
            });

          // 保存sku的属性信息
          sku.getAttr().forEach(attr ->{
              SkuSaleAttrValueEntity attrValue = new SkuSaleAttrValueEntity();

              BeanUtils.copyProperties(attr,attrValue);

              attrValue.setSkuId(skuId);
              skuSaleAttrValueDao.insert(attrValue);
          });

          // 保存需要远程保存的数据信息
            SkuReductionTo skuReductionTo = new SkuReductionTo();
            BeanUtils.copyProperties(sku, skuReductionTo);
            skuReductionTo.setSkuId(skuId);
            skuReductionTos.add(skuReductionTo);
        });

        // 调用Fegin接口远程保存

        Bounds bounds = spuSaveVo.getBounds();
        BoundsTo boundsTo = new BoundsTo();

        boundsTo.setBuyBounds(bounds.getBuyBounds());
        boundsTo.setGrowBounds(bounds.getGrowBounds());
        boundsTo.setSpuId(spuInfoId);

        R r = boundsClients.saveBounds(boundsTo);

        R r1 = boundsClients.saveSkuReductionToBatch(skuReductionTos);

    }

    @Override
    public PageUtils queryPageWithCondition(Map<String, Object> params) {

        String brandId = Optional.ofNullable(params.get("brandId")).map(o -> (String) o).filter(o->!"0".equals(o)).orElse("");
        String catelogId = Optional.ofNullable(params.get("catelogId")).map(o -> (String) o).filter(o->!"0".equals(o)).orElse("");
        String status = Optional.ofNullable(params.get("status")).map(o -> (String) o).orElse("");
        String key = Optional.ofNullable(params.get("key")).map(o -> (String) o).orElse("");


        LambdaQueryWrapper<SpuInfoEntity> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(!StringUtils.isEmpty(brandId),SpuInfoEntity::getBrandId,brandId);
        queryWrapper.eq(!StringUtils.isEmpty(catelogId),SpuInfoEntity::getCatalogId,catelogId);
        queryWrapper.eq(!StringUtils.isEmpty(status), SpuInfoEntity::getPublishStatus,status);
        queryWrapper.and(!StringUtils.isEmpty(key),w ->{
            w.like(SpuInfoEntity::getId,key).or().like(SpuInfoEntity::getSpuName,key);
        });

        Page<SpuInfoEntity> page = new ParamsUtils<SpuInfoEntity>().getPage(params);

        this.page(page,queryWrapper);

        PageUtils pageUtils = new PageUtils(page);


        return pageUtils;
    }



    @Override
    public Boolean up(Long spuId) {

        List<ProductEsModel> skus = skuInfoDao.selectSkuInfoForProductEsModel(spuId);

        ProductEsModel productEsModel = this.baseMapper.selectSpuInfoForProductEsModel(spuId);

        String brandImage = productEsModel.getBrandImage();
        String brandName = productEsModel.getBrandName();
        String categoryName = productEsModel.getCategoryName();

        BulkRequest bulkRequest = new BulkRequest();

        skus.forEach(sku ->{
            sku.setBrandName(brandName);
            sku.setCategoryName(categoryName);
            sku.setBrandImage(brandImage);
            sku.setHotScore(0l);

            IndexRequest indexRequest = new IndexRequest(ElasticsearchOption.PRODUCT_INDEX);
            indexRequest.id(sku.getSkuId().toString());
            indexRequest.source(JSON.toJSONString(sku), XContentType.JSON);

            bulkRequest.add(indexRequest);
        });

        try {
            BulkResponse bulkResponse = esClient.bulk(bulkRequest, ElasticsearchOption.COMMON_OPTIONS);

            if (bulkResponse.hasFailures()) {
                for (BulkItemResponse item : bulkResponse.getItems()) {
                    if (item.isFailed()) {
                        log.info("{} 上架失败, 错误信息为: {}",item.getId() ,item.getFailureMessage());
                    }
                }
            }
        } catch (IOException e) {
            return false;
        }

        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        spuInfoEntity.setId(spuId);
        spuInfoEntity.setPublishStatus(1);
        this.baseMapper.updateById(spuInfoEntity);

        return true;
    }

}