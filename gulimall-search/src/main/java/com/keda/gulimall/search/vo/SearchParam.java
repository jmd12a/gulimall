package com.keda.gulimall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Jmd
 * @create 2024-05-2024/5/23-10:29
 * @Description：
 */

/*
*   keyword: skuTitle 检索关键词
*   catalog3Id: 三级分类的id
*   sort: 排序属性名_升序或降序 saleCount_asc
*   hasStock: 是否有库存
*   brandId: brandId=1&brandId=2: 检索品牌的id,可以多个相同的参数名使用不同的值,并由数组类型的接收
*   price: 1_500 (1,500) , _500 (< 500), 500_ (>500)
*   attr: attr:1_安卓 1为attr的类型,后面是attr的值 attr:2_7寸:8寸 值可以有多个,中间用 ':'分割
*   pageNum:页码
* */
@Data
public class SearchParam {

    // 检索关键词, skuTitle
    private String keyword;
    // 三级分类id
    private Long catalog3Id;
    // 排序类型
    private String sort;
    // 是否有库存
    private Integer hasStock ;

    // 检索条件
    private List<Long> brandId;
    private String price;
    private List<String> attrs;

    private Integer pageNum;
    private Integer pageSize;

}
