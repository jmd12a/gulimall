package com.keda.gulimall.ware.vo;

import lombok.Data;

import java.util.ArrayList;

/**
 * @author Jmd
 * @create 2023-10-2023/10/31-22:33
 * @Description：
 */

@Data
public class MergeVo {

    private Long purchaseId;
    private ArrayList<Integer> items;
}
