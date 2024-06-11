package com.keda.gulimall.fegin.feginClients;

import com.keda.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Jmd
 * @create 2024-03-2024/3/27-19:02
 * @Description：
 */
/*
*  Fegin的调用流程
*       1. 首先构造RequestTemplate对象，其中存储请求地址，请求参数，请求体等内容
*       2. 然后使用executeAndDecode执行方法，并获取返回值
*       3. 在执行过程中，如果出现异常，那么会进行重试，重试就是先调用retryer.continueOrPropagate方法，似乎是休眠一段时间，
*       休眠结束后，如果没有出现异常，那么就再次执行executeAndDecode方法
* */
@FeignClient("goods-service")
@RequestMapping("product/skuinfo")
public interface PmsSkuInfoClient {

    /*
    * 1. product/skuinfo 直接发请求
    * 2. api/"product/skuinfo 通过网关发请求
    * */
    @RequestMapping("/name/{skuId}")
    // @RequiresPermissions("goods:skuinfo:info")
    public R queryName(@PathVariable("skuId") Long skuId );

    @RequestMapping("/test/generics")
    public R testGenerics(@RequestParam("spuId") Long spuId);

    @RequestMapping("/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);

}
