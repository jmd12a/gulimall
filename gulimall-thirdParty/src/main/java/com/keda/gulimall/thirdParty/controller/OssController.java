package com.keda.gulimall.thirdParty.controller;


import com.aliyun.oss.model.PutObjectRequest;
import com.keda.common.utils.R;
import com.keda.gulimall.thirdParty.service.OssService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("thirdparty/oss")
public class OssController {

    @Resource
    public OssService ossService;

    @RequestMapping("/policy")
    public R uploadWithOss(){
        Map respMap = ossService.upload();
        return R.ok().put("data", respMap);
    }
}
