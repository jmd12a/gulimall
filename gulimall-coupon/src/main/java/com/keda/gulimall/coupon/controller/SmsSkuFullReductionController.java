package com.keda.gulimall.coupon.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.keda.common.to.SkuReductionTo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.keda.gulimall.coupon.entity.SmsSkuFullReductionEntity;
import com.keda.gulimall.coupon.service.SmsSkuFullReductionService;
import com.keda.common.utils.PageUtils;
import com.keda.common.utils.R;



/**
 * 商品满减信息
 *
 * @author jmd
 * @email 928814697@qq.com
 * @date 2023-05-13 23:34:52
 */
@RestController
@RequestMapping("coupon/smsskufullreduction")
public class SmsSkuFullReductionController {
    @Autowired
    private SmsSkuFullReductionService smsSkuFullReductionService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("coupon:smsskufullreduction:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = smsSkuFullReductionService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("coupon:smsskufullreduction:info")
    public R info(@PathVariable("id") Long id){
		SmsSkuFullReductionEntity smsSkuFullReduction = smsSkuFullReductionService.getById(id);

        return R.ok().put("smsSkuFullReduction", smsSkuFullReduction);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("coupon:smsskufullreduction:save")
    public R save(@RequestBody SmsSkuFullReductionEntity smsSkuFullReduction){
		smsSkuFullReductionService.save(smsSkuFullReduction);

        return R.ok();
    }

    @PostMapping("/saveBatch")
    // @RequiresPermissions("coupon:smsskufullreduction:save")
    public R saveSkuReductionToBatch(@RequestBody List<SkuReductionTo> SkuReductionTos){
        smsSkuFullReductionService.saveSkuReductionToBatch(SkuReductionTos);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("coupon:smsskufullreduction:update")
    public R update(@RequestBody SmsSkuFullReductionEntity smsSkuFullReduction){
		smsSkuFullReductionService.updateById(smsSkuFullReduction);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("coupon:smsskufullreduction:delete")
    public R delete(@RequestBody Long[] ids){
		smsSkuFullReductionService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
