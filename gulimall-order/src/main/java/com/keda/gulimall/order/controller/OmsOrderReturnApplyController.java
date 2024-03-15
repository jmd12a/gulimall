package com.keda.gulimall.order.controller;

import java.util.Arrays;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.keda.gulimall.order.entity.OmsOrderReturnApplyEntity;
import com.keda.gulimall.order.service.OmsOrderReturnApplyService;
import com.keda.common.utils.PageUtils;
import com.keda.common.utils.R;



/**
 * 订单退货申请
 *
 * @author jmd
 * @email 928814697@qq.com
 * @date 2023-05-13 23:09:17
 */
@RestController
@RequestMapping("order/omsorderreturnapply")
public class OmsOrderReturnApplyController {
    @Autowired
    private OmsOrderReturnApplyService omsOrderReturnApplyService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("order:omsorderreturnapply:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = omsOrderReturnApplyService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("order:omsorderreturnapply:info")
    public R info(@PathVariable("id") Long id){
		OmsOrderReturnApplyEntity omsOrderReturnApply = omsOrderReturnApplyService.getById(id);

        return R.ok().put("omsOrderReturnApply", omsOrderReturnApply);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("order:omsorderreturnapply:save")
    public R save(@RequestBody OmsOrderReturnApplyEntity omsOrderReturnApply){
		omsOrderReturnApplyService.save(omsOrderReturnApply);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("order:omsorderreturnapply:update")
    public R update(@RequestBody OmsOrderReturnApplyEntity omsOrderReturnApply){
		omsOrderReturnApplyService.updateById(omsOrderReturnApply);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("order:omsorderreturnapply:delete")
    public R delete(@RequestBody Long[] ids){
		omsOrderReturnApplyService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
