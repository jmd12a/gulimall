package com.keda.gulimall.goods.common;

import lombok.Data;
import org.checkerframework.checker.units.qual.A;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Jmd
 * @create 2024-04-2024/4/19-14:38
 * @Description：
 */
@Data
public class ProductEsModel {

    private Long skuId;

    private Long spuId;

    private String skuTitle;

    private BigDecimal skuPrice;

    private String skuImage;

    private Long saleCount;

    private Boolean hasStock;

    private Long hotScore;

    private Long BrandId;

    private Long CategoryId;

    private String brandName;

    private String brandImage;

    private String CategoryName;

    private List<Attr> attrs;

    // public保证其他的类对这个类的操作权限，可以进行序列化反序列化等操作，静态的包装这个类的加载


}
