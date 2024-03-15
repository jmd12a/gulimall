package com.keda.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Jmd
 * @create 2023-10-2023/10/24-16:35
 * @Description：
 */

@Data
public class BoundsTo {

    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
    
}
