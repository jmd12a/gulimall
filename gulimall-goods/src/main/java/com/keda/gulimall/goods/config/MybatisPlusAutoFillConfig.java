package com.keda.gulimall.goods.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author Jmd
 * @create 2023-10-2023/10/24-11:07
 * @Description：
 */
@Slf4j
public class MybatisPlusAutoFillConfig implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("插入时自动填充属性 'create_Time' 以及 'updateTime'");
        this.strictInsertFill(metaObject,"createTime", Date.class, new Date());
        this.strictInsertFill(metaObject,"updateTime", Date.class, new Date());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("更新时自动填充属性 'updateTime'");
        this.strictUpdateFill(metaObject,"updateTime", Date.class, new Date());
    }
}
