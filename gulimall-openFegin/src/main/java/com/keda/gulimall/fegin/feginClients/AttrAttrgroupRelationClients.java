package com.keda.gulimall.fegin.feginClients;



import com.keda.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("goods-service")
@RequestMapping("goods")
public interface AttrAttrgroupRelationClients {

    @RequestMapping("/attrattrgrouprelation/info/{id}")
    public R info(@PathVariable("id") Long id);
}
