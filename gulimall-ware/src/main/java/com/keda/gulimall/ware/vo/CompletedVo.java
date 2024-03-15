package com.keda.gulimall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @author Jmd
 * @create 2023-11-2023/11/1-15:24
 * @Description：
 */

@Data
public class CompletedVo {

    @NotNull
    private Long purchaseId;

    @NotEmpty
    private List<Map<String,String>> items;
}
