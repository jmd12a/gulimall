package com.keda.gulimall.goods.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 商品三级分类
 * 
 * @author jmd
 * @email 928814697@qq.com
 * @date 2023-05-13 22:41:21
 */
@Data
@TableName("pms_category")
public class CategoryEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 分类id
	 */
	@TableId( type = IdType.AUTO) // 使用type = IdType.AUTO来标注这个字段是数据库id自增的，然后把数据库中的id字段设置为自增，防止没有id
	// 和mybatisPlus的全局配置不同，全局配置的id-type: AUTO是mybatis-Plus自动给Id赋值
	private Long catId;
	/**
	 * 分类名称
	 */
	private String name;
	/**
	 * 父分类id
	 */
	private Long parentCid;
	/**
	 * 层级
	 */
	private Integer catLevel;
	/**
	 * 是否显示[0-不显示，1显示]
	 */
	private Integer showStatus;
	/**
	 * 排序
	 */
	private Integer sort;
	/**
	 * 图标地址
	 */
	private String icon;
	/**
	 * 计量单位
	 */
	private String productUnit;
	/**
	 * 商品数量
	 */
	private Integer productCount;

	@JsonInclude(value = JsonInclude.Include.NON_EMPTY) // 该字段的数组不为空且不为空数组的时候才转换到json中数据返回
	@TableField(exist = false) // 不是表中的字段
	private List<CategoryEntity> children;
}
