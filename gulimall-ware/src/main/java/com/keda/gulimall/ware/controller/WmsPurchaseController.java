package com.keda.gulimall.ware.controller;

import java.util.Arrays;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keda.common.utils.ParamsUtils;
import com.keda.gulimall.ware.vo.CompletedVo;
import com.keda.gulimall.ware.vo.MergeVo;
import com.keda.gulimall.ware.vo.ReceiveVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.keda.gulimall.ware.entity.WmsPurchaseEntity;
import com.keda.gulimall.ware.service.WmsPurchaseService;
import com.keda.common.utils.PageUtils;
import com.keda.common.utils.R;



/**
 * 采购信息
 *
 * @author jmd
 * @email 928814697@qq.com
 * @date 2023-05-13 23:20:21
 */
@RestController
@RequestMapping("ware/purchase")
public class WmsPurchaseController {
    @Autowired
    private WmsPurchaseService wmsPurchaseService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("ware:wmspurchase:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wmsPurchaseService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("ware:wmspurchase:info")
    public R info(@PathVariable("id") Long id){
		WmsPurchaseEntity wmsPurchase = wmsPurchaseService.getById(id);

        return R.ok().put("wmsPurchase", wmsPurchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("ware:wmspurchase:save")
    public R save(@RequestBody WmsPurchaseEntity wmsPurchase){
		wmsPurchaseService.save(wmsPurchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("ware:wmspurchase:update")
    public R update(@RequestBody WmsPurchaseEntity wmsPurchase){
		wmsPurchaseService.updateById(wmsPurchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("ware:wmspurchase:delete")
    public R delete(@RequestBody Long[] ids){
		wmsPurchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @RequestMapping("/unreceive/list")
    public PageUtils unreceiveList(@RequestParam Map<String, Object> params){

        Page<WmsPurchaseEntity> page = ParamsUtils.acqPage(params);

        wmsPurchaseService.page(page,new QueryWrapper<WmsPurchaseEntity>().in("status", Arrays.asList(0, 1)));


        return new PageUtils(page);

    }

    @PostMapping("/merge")
    public R merge(@RequestBody MergeVo merge){

        wmsPurchaseService.merge(merge);

        return R.ok();
    }

    @PostMapping("/receive")
    public R receivePurchase(@RequestBody @Validated ReceiveVo receiveVo){

        wmsPurchaseService.receivePurchase(receiveVo);

        return R.ok();
    }

    @PostMapping("/completed")
    public R completedPurchase(@RequestBody @Validated CompletedVo completedVo){

        wmsPurchaseService.completedPurchase(completedVo);
        return R.ok();
    }

}
