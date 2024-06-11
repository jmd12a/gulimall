package com.keda.gulimall.goods.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.keda.gulimall.goods.vo.SpuSaveVo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import com.keda.gulimall.goods.entity.SkuInfoEntity;
import com.keda.gulimall.goods.service.SkuInfoService;
import com.keda.common.utils.PageUtils;
import com.keda.common.utils.R;



/**
 * sku信息
 *
 * @author jmd
 * @email 928814697@qq.com
 * @date 2023-05-13 22:41:21
 */
@RestController
@RequestMapping("product/skuinfo")
public class SkuInfoController {
    @Autowired
    private SkuInfoService skuInfoService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("goods:skuinfo:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = skuInfoService.queryPageWithCondition(params);

        return R.ok().put("page", page);
    }

    @RequestMapping("/update1")
    // @RequiresPermissions("goods:skuinfo:update")
    public R updateForCache(){
        skuInfoService.updateDetailById();

        return R.ok();
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{skuId}")
    // @RequiresPermissions("goods:skuinfo:info")
    public R info(@PathVariable("skuId") Long skuId){
		SkuInfoEntity skuInfo = skuInfoService.getById(skuId);

        return R.ok().put("skuInfo", skuInfo);
    }

    @RequestMapping("/name/{skuId}")
    // @RequiresPermissions("goods:skuinfo:info")
    public R queryName(@PathVariable("skuId") Long skuId){
        SkuInfoEntity skuInfo = skuInfoService.getById(skuId);

        return R.ok().put("name", skuInfo.getSkuName());
    }

    /**
     * 修改
     */

    @RequestMapping("/update")
    // @RequiresPermissions("goods:skuinfo:update")
    public R update(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.updateById(skuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("goods:skuinfo:delete")
    public R delete(@RequestBody Long[] skuIds){
		skuInfoService.removeByIds(Arrays.asList(skuIds));

        return R.ok();
    }

    @RequestMapping("/test/generics")
    public R testGenerics(@RequestParam("spuId") Long spuId){

        List<SkuInfoEntity> skus = skuInfoService.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));

        return R.ok().put("data", skus);
    }

}
