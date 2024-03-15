package com.keda.gulimall.ware.dao;

import com.keda.gulimall.ware.entity.WmsPurchaseEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

/**
 * 采购信息
 * 
 * @author jmd
 * @email 928814697@qq.com
 * @date 2023-05-13 23:20:21
 */
@Mapper
public interface WmsPurchaseDao extends BaseMapper<WmsPurchaseEntity> {

    void addRepertoryByPurDetailId(@Param("detailIds") ArrayList<Long> sucPurDetailIds);
}
