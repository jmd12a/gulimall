package com.keda.gulimall.coupon.controller;

import java.util.Arrays;
import java.util.Map;

import com.keda.gulimall.fegin.feginClients.AttrAttrgroupRelationClients;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.keda.gulimall.coupon.entity.SmsCouponEntity;
import com.keda.gulimall.coupon.service.SmsCouponService;
import com.keda.common.utils.PageUtils;
import com.keda.common.utils.R;



/**
 * 优惠券信息
 *
 * @author jmd
 * @email 928814697@qq.com
 * @date 2023-05-13 23:34:52
 */
@RestController
@RequestMapping("coupon/smscoupon")
@Slf4j
public class SmsCouponController {
    @Autowired
    private SmsCouponService smsCouponService;

    @Autowired
    private AttrAttrgroupRelationClients attrAttrgroupRelationClients;


    @RequestMapping("/testFegin/{id}")
    public R testFegin(@PathVariable("id") Long id){
        R info = attrAttrgroupRelationClients.info(1l);
        log.info("openFeign测试成功");
        return info;
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("coupon:smscoupon:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = smsCouponService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("coupon:smscoupon:info")
    public R info(@PathVariable("id") Long id){
		SmsCouponEntity smsCoupon = smsCouponService.getById(id);

        return R.ok().put("smsCoupon", smsCoupon);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("coupon:smscoupon:save")
    public R save(@RequestBody SmsCouponEntity smsCoupon){
		smsCouponService.save(smsCoupon);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("coupon:smscoupon:update")
    public R update(@RequestBody SmsCouponEntity smsCoupon){
		smsCouponService.updateById(smsCoupon);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("coupon:smscoupon:delete")
    public R delete(@RequestBody Long[] ids){
		smsCouponService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
