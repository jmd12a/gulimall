package com.keda.gulimall.goods.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Jmd
 * @create 2024-04-2024/4/22-9:00
 * @Description：
 */

@Data
public class CatalogVo {

    private Long id;
    private Long parentId;
    private String name;
    private List<CatalogVo> children;
}
