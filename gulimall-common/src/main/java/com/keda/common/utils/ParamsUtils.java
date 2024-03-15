package com.keda.common.utils;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.tomcat.util.security.Escape;
import org.springframework.util.StringUtils;

import java.util.Map;

// 解析前段的代码，并构建page对象
public class ParamsUtils<T> {

    public static Page acqPage(Map map){
        Integer pageNum = 1 ;
        Integer pageSize = 10;




        if (map.get("page") != null){
            pageNum =Integer.parseInt((String) map.get("page")) ;
        }

        if (map.get("limit") != null){
            pageSize = Integer.parseInt((String)map.get("limit")) ;
        }

        Page page = new Page<>(pageNum, pageSize);



        String sidx = (String) map.get("sidx");
        String order = (String) map.get("order");

        // 如果排序字段和排序的方式都不为空则进行排序
        if (!StringUtils.isEmpty(sidx) && !StringUtils.isEmpty(order)){
            if (order.equalsIgnoreCase("asc")){
                page.addOrder(OrderItem.asc(sidx));
            }else if (order.equalsIgnoreCase("desc")){
                page.addOrder(OrderItem.desc(sidx));
            }
        }else if (!StringUtils.isEmpty(sidx)){ // 如果字段不为空，排序方式为空，默认升序排序
            page.addOrder(OrderItem.asc(sidx));
        }

        return page;

    }



    public Page<T>  getPage(Map map){
        Integer pageNum = 1 ;
        Integer pageSize = 10;




        if (map.get("page") != null){
            pageNum =Integer.parseInt((String) map.get("page")) ;
        }

        if (map.get("limit") != null){
            pageSize = Integer.parseInt((String)map.get("limit")) ;
        }

        Page<T> page = new Page<>(pageNum, pageSize);



        String sidx = (String) map.get("sidx");
        String order = (String) map.get("order");

        // 如果排序字段和排序的方式都不为空则进行排序
        if (!StringUtils.isEmpty(sidx) && !StringUtils.isEmpty(order)){
            if (order.equalsIgnoreCase("asc")){
                page.addOrder(OrderItem.asc(sidx));
            }else if (order.equalsIgnoreCase("desc")){
                page.addOrder(OrderItem.desc(sidx));
            }
        }else if (!StringUtils.isEmpty(sidx)){ // 如果字段不为空，排序方式为空，默认升序排序
            page.addOrder(OrderItem.asc(sidx));
        }

        return page;

    }
}
