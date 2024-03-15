package com.keda.gulimall.goods.vo;

import com.keda.gulimall.goods.entity.AttrEntity;
import com.keda.gulimall.goods.entity.AttrGroupEntity;
import lombok.Data;

import java.util.List;

/**
 * @author Jmd
 * @create 2023-10-2023/10/20-19:03
 * @Description：
 */
@Data
public class AttrGroupVo extends AttrGroupEntity {

    private List<AttrEntity> attrs;

}
