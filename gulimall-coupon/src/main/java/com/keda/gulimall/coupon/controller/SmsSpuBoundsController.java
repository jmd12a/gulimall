package com.keda.gulimall.coupon.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.keda.common.to.BoundsTo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.keda.gulimall.coupon.entity.SmsSpuBoundsEntity;
import com.keda.gulimall.coupon.service.SmsSpuBoundsService;
import com.keda.common.utils.PageUtils;
import com.keda.common.utils.R;



/**
 * 商品spu积分设置
 *
 * @author jmd
 * @email 928814697@qq.com
 * @date 2023-05-13 23:34:52
 */
@RestController
@RequestMapping("coupon/smsspubounds")
public class SmsSpuBoundsController {
    @Autowired
    private SmsSpuBoundsService smsSpuBoundsService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("coupon:smsspubounds:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = smsSpuBoundsService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("coupon:smsspubounds:info")
    public R info(@PathVariable("id") Long id){
		SmsSpuBoundsEntity smsSpuBounds = smsSpuBoundsService.getById(id);

        return R.ok().put("smsSpuBounds", smsSpuBounds);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("coupon:smsspubounds:save")
    public R save(@RequestBody SmsSpuBoundsEntity smsSpuBounds){
        smsSpuBounds.setWork(1); // 是否起作用,默认是起作用的
		smsSpuBoundsService.save(smsSpuBounds);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("coupon:smsspubounds:update")
    public R update(@RequestBody SmsSpuBoundsEntity smsSpuBounds){
		smsSpuBoundsService.updateById(smsSpuBounds);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("coupon:smsspubounds:delete")
    public R delete(@RequestBody Long[] ids){
		smsSpuBoundsService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }


    /*@RequestMapping("/coupon/smsspubounds/saveByFegin")
    public void saveSpuBounds(@RequestBody BoundsTo boundsTo){
        SmsSpuBoundsEntity boundsEntity = new SmsSpuBoundsEntity();

        BeanUtils.copyProperties(boundsTo,boundsEntity);
        boundsEntity.setWork(1);

    }*/

}
