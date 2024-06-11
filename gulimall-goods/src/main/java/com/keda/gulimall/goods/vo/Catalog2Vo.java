package com.keda.gulimall.goods.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Jmd
 * @create 2024-04-2024/4/22-9:00
 * @Description：
 */

@Data
public class Catalog2Vo {

    private Long id;
    private Long catalog1Id;
    private String name;
    private List<Catalog3Vo> catalog3List;

    @Data
    public static class Catalog3Vo{
        private Long id;
        private Long catalog2Id;
        private String name;

    }
}
