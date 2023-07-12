package com.keda.gulimall.goods.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.keda.gulimall.goods.entity.CategoryEntity;
import com.keda.gulimall.goods.service.CategoryService;
import com.keda.common.utils.PageUtils;
import com.keda.common.utils.R;



/**
 * 商品三级分类
 *
 * @author jmd
 * @email 928814697@qq.com
 * @date 2023-05-13 22:41:21
 */
@RestController
@RequestMapping("product/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 列表
     */
    @RequestMapping("/list/tree")
    public R list(){
        List<CategoryEntity> categoryEntities = categoryService.treeList();


        return R.ok().put("data", categoryEntities);
    }
    /*@RequestMapping("/list")
    @RequiresPermissions("goods:category:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryService.queryPage(params);

        return R.ok().put("page", page);
    }*/


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    @RequiresPermissions("goods:category:info")
    public R info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("category", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("goods:category:save")
    public R save(@RequestBody CategoryEntity category){
		categoryService.save(category);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CategoryEntity category){

        categoryService.updateDetail(category);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    /*@RequestBody注解接收 请求体中的json数据,一般是application/json数据;
    * 该注解后可以使用实体类、数组、Map等来接收数据
    * 需要注意的是，@RequestBody注解，一个方法的形参中只能有一个，并且要能够接收请求体中的所有application/json数据
    *
    * 注: @RequestParam注解可以接收请求头中的单个参数，也可以在后面使用Map接收请求体中的所有参数，不能使用实体类来接收
    *     如果不使用注解，则可以使用实体类或者单个参数来接收，不能使用Map来接收；一般接收请求头中的参数，或者请求体中的from-data,x-www-from-urlencoded参数
    * */
    public R delete(@RequestBody Long[] catIds){

        List<CategoryEntity> all = categoryService.list(
                new LambdaQueryWrapper<CategoryEntity>()
                        .ne(CategoryEntity::getShowStatus, 0)
                        .ne(CategoryEntity::getCatLevel,1)); // 层级为1的不可能是子分类，无需查出

        Arrays.stream(catIds).forEach(id -> {
            /*
            * 查要删除的菜单是否存在未被逻辑删除的子类，如果存在未被逻辑删除的子类，则无法不能逻辑删除
            * */
            // 方式一：使用数据库查询来获取，效率可能较低
            /*List<CategoryEntity> categoryEntities = categoryService.list(
                    new LambdaQueryWrapper<CategoryEntity>().eq(CategoryEntity::getParentCid, id)
                            .ne(CategoryEntity::getShowStatus,0));*/
            // 方式二：先把数据库中未被逻辑删除的所有数据查出来，在代码层进行操作
            List<CategoryEntity> categoryEntities = all.stream().filter(categoryEntity -> {
                return categoryEntity.getParentCid() == id;
            }).collect(Collectors.toList());

            // 存在子分类的菜单无法被删除
            if (CollectionUtils.isEmpty(categoryEntities)){
                CategoryEntity category = categoryService.getById(id);
                category.setShowStatus(0);
                categoryService.updateById(category);
            }else log.info("该分类存在子分类，无法逻辑删除");
        });

        return R.ok();
    }

    @RequestMapping("/update/sort")
    public R updateSort(@RequestBody List<CategoryEntity> categoryEntityList){
        System.out.println("categoryEntityList.size: " + categoryEntityList.size());
        categoryService.updateSort(categoryEntityList);

        return R.ok();
    }

}
