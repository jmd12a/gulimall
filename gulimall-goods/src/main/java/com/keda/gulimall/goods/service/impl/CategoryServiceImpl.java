package com.keda.gulimall.goods.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.keda.gulimall.goods.dao.CategoryBrandRelationDao;
import com.keda.gulimall.goods.entity.CategoryBrandRelationEntity;
import com.keda.gulimall.goods.vo.CatalogVo;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.keda.common.utils.PageUtils;
import com.keda.common.utils.Query;

import com.keda.gulimall.goods.dao.CategoryDao;
import com.keda.gulimall.goods.entity.CategoryEntity;
import com.keda.gulimall.goods.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Resource
    private CategoryBrandRelationDao relationDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> treeList() {
        // 获取所有showStatus不等于0的，也就是没有被逻辑删除的
        List<CategoryEntity> categoryEntities = baseMapper.selectList(
                new LambdaQueryWrapper<CategoryEntity>()
                        .ne(CategoryEntity::getShowStatus,0)); // 条件构造器传入空就是查询所有

        // 之前没有进行收集，filter和sort生效了但是没有作用到数组categoryEntities，只有通过map()方法对categoryEntities数组中经过filter过滤的元素进行了操作
        List<CategoryEntity> categoryEntityList = categoryEntities.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == 0;
        }).map(categoryEntity -> {
            categoryEntity.setChildren(this.getChildren(categoryEntity, categoryEntities));
            return categoryEntity;
        }).sorted((category1, category2) -> {
            return (category1.getSort() == null ? 0 : category1.getSort()) - (category2.getSort() == null ? 0 : category2.getSort());
        }).collect(Collectors.toList());


        return categoryEntityList;
    }

    @Override
    @Transactional
    public void updateSort(List<CategoryEntity> categoryEntityList) {
        // 更新sort
        categoryEntityList.stream().forEach(categoryEntity -> {
            Long catId = categoryEntity.getCatId();
            baseMapper.update(categoryEntity,
                    new LambdaUpdateWrapper<CategoryEntity>()
                            .eq(CategoryEntity::getCatId,catId));
        });
    }

    @Override
    @Transactional
    public void updateDetail(CategoryEntity category) {
        Long catId = category.getCatId();
        String name = category.getName();

        CategoryEntity originalCategory = baseMapper.selectById(catId);

        baseMapper.updateById(category);


        if (!(StringUtils.isEmpty(name) || name.equals(originalCategory.getName()))){
        // if ((!StringUtils.isEmpty(name)) && (!name.equals(originalCategory.getName())) ){ 这两个是一样的，上边的更简洁
            CategoryBrandRelationEntity relation = new CategoryBrandRelationEntity();
            //relation.setCatelogId(catId);
            relation.setCatelogName(name);

            relationDao.update(relation, new LambdaUpdateWrapper<CategoryBrandRelationEntity>()
                    //.set(CategoryBrandRelationEntity::getCatelogName,name)
                    .eq(CategoryBrandRelationEntity::getCatelogId,catId));
        }

    }

    @Override
    public List<CategoryEntity> getAllFristCategory() {
        List<CategoryEntity> categoryEntityList =
                baseMapper.selectList(new LambdaQueryWrapper<CategoryEntity>().eq(CategoryEntity::getCatLevel, 1));
        return categoryEntityList;
    }

    @Override
    public CatalogVo selectSubCatalogByStairId(Long stairId) {
        CategoryEntity parentCategory = baseMapper.selectById(stairId);
        CatalogVo parentCatalog = new CatalogVo();
        parentCatalog.setId(parentCategory.getCatId());
        parentCatalog.setName(parentCategory.getName());
        parentCatalog.setParentId(0l);

        recursivelySelectSubCatalogByStairId(parentCatalog);


        return parentCatalog;
    }

    public void recursivelySelectSubCatalogByStairId(CatalogVo catalogVo){

        List<CategoryEntity> categoryEntities =
                baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", catalogVo.getId()));

        if (categoryEntities.isEmpty()){
            return;
        }

        List<CatalogVo> subList = categoryEntities.stream().map(categoryEntity -> {
            CatalogVo catalog = new CatalogVo();
            catalog.setId(categoryEntity.getCatId());
            catalog.setName(categoryEntity.getName());
            catalog.setParentId(catalogVo.getId());
            return catalog;
        }).collect(Collectors.toList());

        catalogVo.setChildren(subList);

        for (CatalogVo subCatalog : subList) {
            recursivelySelectSubCatalogByStairId(subCatalog);
        }

    }

    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {

        // List<CategoryEntity> categoryEntities = baseMapper.selectList(new LambdaQueryWrapper<CategoryEntity>().eq(CategoryEntity::getParentCid, root.getCatId()));

        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == root.getCatId();
        }).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(children)) {
            return null;
        } // 递归结束的条件

        children = children.stream().map(categoryEntity -> {
            categoryEntity.setChildren(this.getChildren(categoryEntity, all));
            return categoryEntity;
        }).sorted((category1, category2) -> {
            return (category1.getSort()==null?0:category1.getSort()) - (category2.getSort()==null?0:category2.getSort());
        }).collect(Collectors.toList());

        return children;
    }

}