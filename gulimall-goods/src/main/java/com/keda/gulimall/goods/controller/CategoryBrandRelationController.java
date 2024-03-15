package com.keda.gulimall.goods.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.keda.gulimall.goods.entity.BrandEntity;
import com.keda.gulimall.goods.entity.CategoryEntity;
import com.keda.gulimall.goods.service.BrandService;
import com.keda.gulimall.goods.service.CategoryService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.keda.gulimall.goods.entity.CategoryBrandRelationEntity;
import com.keda.gulimall.goods.service.CategoryBrandRelationService;
import com.keda.common.utils.PageUtils;
import com.keda.common.utils.R;

import javax.annotation.Resource;


/**
 * 品牌分类关联
 *
 * @author jmd
 * @email 928814697@qq.com
 * @date 2023-05-13 22:41:21
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }

    @RequestMapping("/catelog/list")
    public R catelogList(@RequestParam("brandId") Integer id){

        List<CategoryBrandRelationEntity> categoryBrandRelationEntityList = categoryBrandRelationService.list(
                new LambdaUpdateWrapper<CategoryBrandRelationEntity>().eq(CategoryBrandRelationEntity::getBrandId, id));

        return R.ok().put("data", categoryBrandRelationEntityList);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // // @RequiresPermissions("goods:categorybrandrelation:info")
    public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // // @RequiresPermissions("goods:categorybrandrelation:save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
        CategoryEntity category = categoryService.getById(categoryBrandRelation.getCatelogId());
        BrandEntity brand = brandService.getById(categoryBrandRelation.getBrandId());

        categoryBrandRelation.setCatelogName(category.getName());
        categoryBrandRelation.setBrandName(brand.getName());
        categoryBrandRelationService.save(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 修改
     */
    // @RequestMapping("/update")
    // // @RequiresPermissions("goods:categorybrandrelation:update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // // @RequiresPermissions("goods:categorybrandrelation:delete")
    public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @RequestMapping("/brands/list")
    public R getBrandsListByCategoryId(@RequestParam("catId") Long categoryId){

        List<CategoryBrandRelationEntity> relationList = categoryBrandRelationService.list(new LambdaUpdateWrapper<CategoryBrandRelationEntity>()
                .eq(CategoryBrandRelationEntity::getCatelogId, categoryId));

        List<Long> brandIds = relationList.stream().map(cateBrandRelation -> {
            return cateBrandRelation.getBrandId();
        }).collect(Collectors.toList());

        if (brandIds.size() == 0) {
            return R.ok().put("data",new ArrayList<>());
        }
        List<BrandEntity> brandList = brandService.list(new LambdaUpdateWrapper<BrandEntity>()
                .in(BrandEntity::getBrandId, brandIds));

        return R.ok().put("data",brandList);
    }


}
