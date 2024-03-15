package com.keda.gulimall.goods.controller;

import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keda.common.utils.ParamsUtils;
import com.keda.gulimall.goods.entity.AttrAttrgroupRelationEntity;
import com.keda.gulimall.goods.entity.AttrEntity;
import com.keda.gulimall.goods.service.AttrAttrgroupRelationService;
import com.keda.gulimall.goods.service.AttrService;
import com.keda.gulimall.goods.vo.AttrGroupVo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.keda.gulimall.goods.entity.AttrGroupEntity;
import com.keda.gulimall.goods.service.AttrGroupService;
import com.keda.common.utils.PageUtils;
import com.keda.common.utils.R;

import javax.annotation.Resource;


/**
 * 属性分组
 *
 * @author jmd
 * @email 928814697@qq.com
 * @date 2023-05-13 22:41:21
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Resource
    private AttrAttrgroupRelationService relationService;

    @Resource
    private AttrService attrService;

    /**
     * 列表
     */
    @GetMapping("/list/{id}")
    public R list(@RequestParam Map<String, Object> params, @PathVariable("id") Long id){
        // PageUtils page = attrGroupService.queryPage(params);

        PageUtils page = attrGroupService.queryPageWithCategoryId(params,id);

        return R.ok().put("page", page);
    }

    @GetMapping("/{attrGroupId}/attr/relation")
    public R getRelation(@PathVariable("attrGroupId") Integer attrGroupId){
        List<AttrEntity> attrList = relationService.queryAttrByAttrGroupId(attrGroupId);
        return R.ok().put("data",attrList);
    }


    /**
     * 信息
     */
    @GetMapping ("/info/{attrGroupId}")
    // @RequiresPermissions("goods:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);

        ArrayList<Long> cateLogPath = new ArrayList<>();

        // 给attrGroup设置cateLogPath
        attrGroupService.getCateLogPath(attrGroup.getCatelogId(), cateLogPath);
        Collections.reverse(cateLogPath);
        attrGroup.setCatelogPath(cateLogPath.toArray(new Long[cateLogPath.size()]));

        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    // @RequiresPermissions("goods:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    // @RequiresPermissions("goods:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    // @RequiresPermissions("goods:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

    @GetMapping("/{attrgroupId}/noattr/relation")
    // 当使用两个@RequestParam注解，一个注解指定接收某个变量，一个注解使用map接收所有变量时，被指定接收的变量两者都能接收到
    public R getNoRelationAttr(@PathVariable("attrgroupId") Long attrgroupId, @RequestParam("key") String key,@RequestParam Map<String,Object> map){

        Page page = attrGroupService.getAttrWithoutRelation(attrgroupId,key,map);

        /*AttrGroupEntity attrGroup = attrGroupService.getById(attrgroupId);

        List<AttrAttrgroupRelationEntity> attrgroupRelationEntities = relationService.list(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                .eq(AttrAttrgroupRelationEntity::getAttrGroupId, attrgroupId));

        List<Long> attrIds = attrgroupRelationEntities.stream().map(relation -> {
            return relation.getAttrId();
        }).collect(Collectors.toList());

        LambdaQueryWrapper<AttrEntity> wrapper = new LambdaQueryWrapper<AttrEntity>()
                .notIn(attrIds.size() > 0, AttrEntity::getAttrId, attrIds)
                .like((!StringUtils.isEmpty(key)), AttrEntity::getAttrName, key)
                .eq(AttrEntity::getCatelogId,attrGroup.getCatelogId());

        Page<AttrEntity> page = attrService.page(new ParamsUtils<AttrEntity>().getPage(map), wrapper);*/

        return R.ok().put("page",new PageUtils(page));
    }

    @PostMapping("/attr/relation/delete")
    public R deleteAttrRelation(@RequestBody List<AttrAttrgroupRelationEntity> relationEntities){
        boolean remove = true;

        AttrAttrgroupRelationEntity relationEntity;

        for (int i = 0; i < relationEntities.size(); i++) {
            relationEntity = relationEntities.get(i);

            Boolean tmpRemove = relationService.remove(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                    .eq(AttrAttrgroupRelationEntity::getAttrId, relationEntity.getAttrId())
                    .eq(AttrAttrgroupRelationEntity::getAttrGroupId, relationEntity.getAttrGroupId()));
            if (!tmpRemove){
                remove = false;
            }
        }

        if (remove){
            return R.ok();
        }else return R.error();

    }

    @PostMapping("/attr/relation")
    public R addRelation(@RequestBody List<AttrAttrgroupRelationEntity> relationList){
        boolean b = relationService.saveBatch(relationList);
        if (b){
            return R.ok();
        }
        return R.error();
    }

    @GetMapping("/{categoryId}/withattr")
    public R getAttrGroupWithAttr(@PathVariable("categoryId") String categoryId){

        List<AttrGroupVo> attrGroups = attrGroupService.getAttrGroupWithAttr(categoryId);

        return R.ok().put("data",attrGroups);
    }

}
