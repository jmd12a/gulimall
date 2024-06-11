package com.keda.gulimall.goods.controller;

import java.util.Arrays;
import java.util.Map;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keda.gulimall.goods.vo.SpuSaveVo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.keda.gulimall.goods.entity.SpuInfoEntity;
import com.keda.gulimall.goods.service.SpuInfoService;
import com.keda.common.utils.PageUtils;
import com.keda.common.utils.R;



/**
 * spu信息
 *
 * @author jmd
 * @email 928814697@qq.com
 * @date 2023-05-13 22:41:20
 */
@RestController
@RequestMapping("product/spuinfo")
public class SpuInfoController {
    @Autowired
    private SpuInfoService spuInfoService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("goods:spuinfo:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = spuInfoService.queryPageWithCondition(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("goods:spuinfo:info")
    public R info(@PathVariable("id") Long id){
		SpuInfoEntity spuInfo = spuInfoService.getById(id);

        return R.ok().put("spuInfo", spuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("goods:skuinfo:save")
    public R save(@RequestBody SpuSaveVo saveVo){
        // skuInfoService.save(saveVo);
        spuInfoService.saveDetail(saveVo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("goods:spuinfo:update")
    public R update(@RequestBody SpuInfoEntity spuInfo){
		spuInfoService.updateById(spuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("goods:spuinfo:delete")
    public R delete(@RequestBody Long[] ids){
		spuInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @PostMapping("/{spuId}/up")
    public R upSpu(@PathVariable Long spuId){
        Boolean successfulUp = spuInfoService.up(spuId);

        if (successfulUp)
            return R.ok();
        else
            return R.error();
    }

}
