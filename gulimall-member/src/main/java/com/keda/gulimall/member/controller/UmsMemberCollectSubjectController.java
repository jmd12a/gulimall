package com.keda.gulimall.member.controller;

import java.util.Arrays;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.keda.gulimall.member.entity.UmsMemberCollectSubjectEntity;
import com.keda.gulimall.member.service.UmsMemberCollectSubjectService;
import com.keda.common.utils.PageUtils;
import com.keda.common.utils.R;



/**
 * 会员收藏的专题活动
 *
 * @author jmd
 * @email 928814697@qq.com
 * @date 2023-05-13 23:05:04
 */
@RestController
@RequestMapping("member/membercollectsubject")
public class UmsMemberCollectSubjectController {
    @Autowired
    private UmsMemberCollectSubjectService umsMemberCollectSubjectService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("member:umsmembercollectsubject:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = umsMemberCollectSubjectService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("member:umsmembercollectsubject:info")
    public R info(@PathVariable("id") Long id){
		UmsMemberCollectSubjectEntity umsMemberCollectSubject = umsMemberCollectSubjectService.getById(id);

        return R.ok().put("MemberCollectSubject", umsMemberCollectSubject);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("member:umsmembercollectsubject:save")
    public R save(@RequestBody UmsMemberCollectSubjectEntity umsMemberCollectSubject){
		umsMemberCollectSubjectService.save(umsMemberCollectSubject);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("member:umsmembercollectsubject:update")
    public R update(@RequestBody UmsMemberCollectSubjectEntity umsMemberCollectSubject){
		umsMemberCollectSubjectService.updateById(umsMemberCollectSubject);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("member:umsmembercollectsubject:delete")
    public R delete(@RequestBody Long[] ids){
		umsMemberCollectSubjectService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
