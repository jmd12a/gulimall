package com.keda.gulimall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Jmd
 * @create 2023-11-2023/11/1-11:42
 * @Description：
 */

@Data
public class ReceiveVo {

    @NotNull
    private Long userId;

    @NotEmpty
    private List<Long> purchaseIds;


}
