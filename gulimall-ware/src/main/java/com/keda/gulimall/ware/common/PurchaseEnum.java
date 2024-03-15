package com.keda.gulimall.ware.common;

import lombok.Getter;

/**
 * @author Jmd
 * @create 2023-11-2023/11/1-15:05
 * @Description：
 */
@Getter
public enum PurchaseEnum {

    NEW(0,"新建"),

    ASSIGNED(1,"已分配"),

    RECEIVED(2,"已领取"),

    COMPLETED(3,"已完成"),

    ABNORMAL(4,"采购不成功");

    private final Integer code;

    private final String message;

    PurchaseEnum(Integer code, String message){
        this.code = code;
        this.message = message;
    }

}
