package com.keda.gulimall.search.vo;

import com.keda.gulimall.search.common.Attr;
import com.keda.gulimall.search.common.ProductEsModel;
import lombok.Data;

import java.util.List;

/**
 * @author Jmd
 * @create 2024-05-2024/5/23-11:27
 * @Description：
 */

@Data
public class SearchResult {

    private List<ProductEsModel> productEsModelList;

    private Integer totalPages; // 总页码
    private Integer pageNum; // 当前页码
    private Integer pageSize; // 每页数据的条数

    private List<Brand> relatedBrand; // 检索内容的相关品牌
    private List<Attr> relatedAttr; // 检索内容的相关属性
    private List<catalogVo> relatedCategory; // 检索内容的相关分类

    @Data
    public static class Brand{ // 设置为public的，方便使用时调用
        private String brandId;
        private String name;
        private String image;
    }

    public static class catalogVo{
        private String catalogId;
        private String catalogName;
    }

}
