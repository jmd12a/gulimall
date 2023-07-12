package com.keda.gulimall.goods.controller;

import java.util.Arrays;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keda.common.vaild.AddVaildGroup;
import com.keda.common.vaild.UpdateStatusVaildGroup;
import com.keda.common.vaild.UpdateVaildGroup;
import com.mysql.cj.util.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.keda.gulimall.goods.entity.BrandEntity;
import com.keda.gulimall.goods.service.BrandService;
import com.keda.common.utils.PageUtils;
import com.keda.common.utils.R;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


/**
 * 品牌
 *
 * @author jmd
 * @email 928814697@qq.com
 * @date 2023-05-13 22:41:21
 */
/*
* JSR303数据校验
*
* */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam("page") Integer pageNum, @RequestParam("limit")Integer pageSize, @RequestParam("key") String brand){
        Page<BrandEntity> brandPage = new Page<BrandEntity>(pageNum,pageSize);

        if (StringUtils.isNullOrEmpty(brand)){
            brandService.page(brandPage); // 这里直接把结果给了brandPage，无需参数接收
        }else {
            brandService.page(brandPage,new LambdaQueryWrapper<BrandEntity>()
                    .like(BrandEntity::getName,brand));
        }


        return R.ok().put("page", brandPage);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
     public R info(@PathVariable("brandId") Long brandId){

		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // AddVaildGroup只是起到一个标识作用，不需要任何方法实现
    public R save(@Validated(AddVaildGroup.class) @RequestBody BrandEntity brand){
        String logo = brand.getLogo();
        String firstLetter = brand.getFirstLetter();

        /*if (StringUtils.isNullOrEmpty(logo)){
            return R.error("logo地址不能为空");
        }else if (StringUtils.isNullOrEmpty(firstLetter) ||  firstLetter.length() != 1 || !Character.isLetter(firstLetter.charAt(0))){
            return R.error("检索首字母不能为空且只能为一位字母");
        }else if (brand.getSort() <= 0){
            return R.error("排序字段必须大于0");
        }*/

		brandService.save(brand);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update( @RequestBody @Validated(UpdateVaildGroup.class) BrandEntity brand){
        /*String logo = brand.getLogo();
        String firstLetter = brand.getFirstLetter();
        if (StringUtils.isNullOrEmpty(logo)){
            return R.error("logo地址不能为空");
        }else if (StringUtils.isNullOrEmpty(firstLetter) ||  firstLetter.length() != 1 || !Character.isLetter(firstLetter.charAt(0))){
            return R.error("检索首字母不能为空且只能为一位字母");
        }else if (brand.getSort() <= 0){
            return R.error("排序字段必须大于0");
        }*/
        // brandService.updateById(brand);
        brandService.updateDetail(brand);

        return R.ok();
    }

    @RequestMapping("/update/status")
    public R updateStatus( @RequestBody @Validated(UpdateStatusVaildGroup.class) BrandEntity brand){
       /* String logo = brand.getLogo();
        String firstLetter = brand.getFirstLetter();
        if (StringUtils.isNullOrEmpty(logo)){
            return R.error("logo地址不能为空");
        }else if (StringUtils.isNullOrEmpty(firstLetter) ||  firstLetter.length() != 1 || !Character.isLetter(firstLetter.charAt(0))){
            return R.error("检索首字母不能为空且只能为一位字母");
        }else if (brand.getSort() <= 0){
            return R.error("排序字段必须大于0");
        }*/

        brandService.update(new LambdaUpdateWrapper<BrandEntity>()
                .set(BrandEntity::getShowStatus,brand.getShowStatus())
                .eq(BrandEntity::getBrandId,brand.getBrandId()));

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("goods:brand:delete")
    public R delete(@RequestBody @NotEmpty Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
