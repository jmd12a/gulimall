package com.keda.gulimall.member.controller;

import
        java.util.Arrays;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.keda.gulimall.member.entity.UmsIntegrationChangeHistoryEntity;
import com.keda.gulimall.member.service.UmsIntegrationChangeHistoryService;
import com.keda.common.utils.PageUtils;
import com.keda.common.utils.R;



/**
 * 积分变化历史记录
 *
 * @author jmd
 * @email 928814697@qq.com
 * @date 2023-05-13 23:05:04
 */
@RestController
@RequestMapping("member/integrationchangehistory")
public class UmsIntegrationChangeHistoryController {
    @Autowired
    private UmsIntegrationChangeHistoryService umsIntegrationChangeHistoryService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("member:umsintegrationchangehistory:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = umsIntegrationChangeHistoryService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("member:umsintegrationchangehistory:info")
    public R info(@PathVariable("id") Long id){
		UmsIntegrationChangeHistoryEntity umsIntegrationChangeHistory = umsIntegrationChangeHistoryService.getById(id);

        return R.ok().put("IntegrationChangeHistory", umsIntegrationChangeHistory);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("member:umsintegrationchangehistory:save")
    public R save(@RequestBody UmsIntegrationChangeHistoryEntity umsIntegrationChangeHistory){
		umsIntegrationChangeHistoryService.save(umsIntegrationChangeHistory);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("member:umsintegrationchangehistory:update")
    public R update(@RequestBody UmsIntegrationChangeHistoryEntity umsIntegrationChangeHistory){
		umsIntegrationChangeHistoryService.updateById(umsIntegrationChangeHistory);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("member:umsintegrationchangehistory:delete")
    public R delete(@RequestBody Long[] ids){
		umsIntegrationChangeHistoryService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
