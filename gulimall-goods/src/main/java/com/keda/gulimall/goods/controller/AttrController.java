package com.keda.gulimall.goods.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.keda.gulimall.goods.entity.ProductAttrValueEntity;
import com.keda.gulimall.goods.service.ProductAttrValueService;
import com.keda.gulimall.goods.vo.AttrVo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.keda.gulimall.goods.entity.AttrEntity;
import com.keda.gulimall.goods.service.AttrService;
import com.keda.common.utils.PageUtils;
import com.keda.common.utils.R;



/**
 * 商品属性
 *
 * @author jmd
 * @email 928814697@qq.com
 * @date 2023-05-13 22:41:21
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService attrValueService;

    /**
     * 列表
     */
    @RequestMapping("/{attrType}/list/{cateLogId}")
    // // @RequiresPermissions("goods:attr:list")
    public R list(@RequestParam Map<String, Object> params,
                  @PathVariable("cateLogId") Long cateLogId,
                  @PathVariable("attrType") String attrType){

        // PageUtils page = attrService.queryPage(params);

        PageUtils page = attrService.queryBasePage(params, cateLogId, attrType);

        return R.ok().put("page", page);
    }

    @GetMapping("/base/listforspu/{spuId}")
    public R baseAttrForSpu(@PathVariable(name = "spuId") Long spuId){

        List<ProductAttrValueEntity> AttrsForSpu = attrValueService.list(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));

        return R.ok().put("data", AttrsForSpu);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    // @RequiresPermissions("goods:attr:info")
    public R info(@PathVariable("attrId") Long attrId){

        AttrVo attrVo = attrService.getInfoById(attrId);

        return R.ok().put("attr", attrVo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // // @RequiresPermissions("goods:attr:save")
    public R save(@RequestBody AttrVo attrVo){
		attrService.saveDetail(attrVo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("goods:attr:update")
    public R update(@RequestBody AttrVo attr){
		attrService.updateDetail(attr);

        return R.ok();
    }

    @PostMapping("/update/{spuId}")
    // @RequiresPermissions("goods:attr:update")
    public R updateAttrValues(@PathVariable(name = "spuId") Long spuId , @RequestBody List<ProductAttrValueEntity> attrValues){

        attrService.updateAttrValues(spuId, attrValues);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("goods:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }



}
