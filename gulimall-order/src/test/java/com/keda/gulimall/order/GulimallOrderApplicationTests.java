package com.keda.gulimall.order;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.keda.common.utils.R;
import com.keda.gulimall.fegin.feginClients.PmsSkuInfoClient;
import com.keda.gulimall.order.entity.SkuInfoEntity;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;


@SpringBootTest
class GulimallOrderApplicationTests {

    @Resource
    private PmsSkuInfoClient pmsSkuInfoClient;

    @Test
    public void test(){

        R r = pmsSkuInfoClient.testGenerics(1718983608961015809l);

        List<SkuInfoEntity> data = getData(r, new TypeReference<List<SkuInfoEntity>>() {

        });

        System.out.println();
    }

    @Test
    public void test1(){

        R r = pmsSkuInfoClient.info(12l);

        SkuInfoEntity object = getObject(r, SkuInfoEntity.class);

        System.out.println();
    }

    // 接口之间数据传输时，对获取到的数据进行类型转换
    public <T> T getObject(R r, Class<T> clazz){

        /*
        Object data = r.get("skuInfo");

        T cast = clazz.cast(data);

        return cast;
        当openFegin将响应的json转换为R（HashMap）时，并不知道其中的内容是什么类型，讲其中的data转换为了LinkedHashMap,因此不能直接强转
        */

        Object skuInfo = r.get("skuInfo");

        /*
        * 1. 先获取object
        * 2. 然后将object转换为json类型
        * 3. 再将json类型的数据转换为SkuInfoEntity类型
        *
        * String jsonString = JSON.toJSONString(skuInfo);
        * String objectString = skuInfo.toString();
        * 如果使用toString()方法转换的话，那么格式不是json格式，再次转换回来的时候就会出现问题
        * */

        return JSON.parseObject(JSON.toJSONString(skuInfo), clazz);



    }

    // 接口之间数据传输时，对获取到的数据进行类型转换， 复杂类型
    public <T> T getData(R r, TypeReference<T> typeReference){

        Object data = r.get("data");

        String jsonString = JSON.toJSONString(data);

        T t = JSON.parseObject(jsonString, typeReference);

        return t;

    }

}
