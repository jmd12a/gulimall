package com.keda.gulimall.goods.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.keda.gulimall.goods.dao.CategoryBrandRelationDao;
import com.keda.gulimall.goods.entity.CategoryBrandRelationEntity;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.keda.common.utils.PageUtils;
import com.keda.common.utils.Query;

import com.keda.gulimall.goods.dao.BrandDao;
import com.keda.gulimall.goods.entity.BrandEntity;
import com.keda.gulimall.goods.service.BrandService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Resource
    private CategoryBrandRelationDao categoryBrandRelationDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                new QueryWrapper<BrandEntity>()
        );

        return new PageUtils(page);
    }

    // 详细更新，在更新brand的同时更新所有和brand相关的表
    @Override
    @Transactional
    public void updateDetail(BrandEntity brand) {
        String brandName = brand.getName();
        Long brandId = brand.getBrandId();

        // 先查出没有修改前的brand，修改后再查查出的就是修改后的，brandName.equals(originalBrand.getName())则永远为真了
        BrandEntity originalBrand = baseMapper.selectById(brandId);

        baseMapper.updateById(brand);// 更新brand表



        if (!(StringUtils.isEmpty(brandName) || brandName.equals(originalBrand.getName()))) {
            // 更新category和brand的关系表中的brandName
            CategoryBrandRelationEntity categoryBrandRelationEntity = new CategoryBrandRelationEntity();
            categoryBrandRelationEntity.setBrandName(brandName);
            // categoryBrandRelationEntity.setBrandId(brand.getBrandId());
            categoryBrandRelationDao.update(categoryBrandRelationEntity,
                    new LambdaUpdateWrapper<CategoryBrandRelationEntity>()
                            .eq(CategoryBrandRelationEntity::getBrandId, brandId));
        }


    }

}