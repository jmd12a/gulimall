package com.keda.gulimall.fegin.feginClients;

import com.keda.common.to.BoundsTo;
import com.keda.common.to.SkuReductionTo;
import com.keda.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Jmd
 * @create 2023-10-2023/10/24-17:23
 * @Description：
 */


@FeignClient("coupon-service")
public interface BoundsClients {

    // 只需要对应数据的属性名和属性类型一样，就可以被解析，不一定非要用一个实体类。
    @PostMapping("coupon/smsspubounds/save")
    R saveBounds(@RequestBody BoundsTo boundsTo);

    @PostMapping("coupon/smsskufullreduction/saveBatch")
    R saveSkuReductionToBatch(@RequestBody List<SkuReductionTo> SkuReductionTos);
}
