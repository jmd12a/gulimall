package com.keda.gulimall.goods.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import com.keda.common.vaild.AddVaildGroup;
import com.keda.common.vaild.ListValue;
import com.keda.common.vaild.UpdateStatusVaildGroup;
import com.keda.common.vaild.UpdateVaildGroup;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 * 
 * @author jmd
 * @email 928814697@qq.com
 * @date 2023-05-13 22:41:21
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	// UpdateVaildGroup.class, UpdateStatusVaildGroup.class起到一个标示的作用，当指定分组是这个的时候起作用
	@NotNull(groups = {UpdateVaildGroup.class, UpdateStatusVaildGroup.class},message = "修改品牌Id不能为空")
	@TableId(type = IdType.AUTO)
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotBlank(message = "新增或修改品牌名不能为空",groups = {AddVaildGroup.class, UpdateVaildGroup.class})
	@Length(message = "品牌名长度不能超过十个字符", max = 10, groups = {AddVaildGroup.class, UpdateVaildGroup.class})
	private String name;
	/**
	 * 品牌logo地址
	 */

	// 这两个加起来是新增时不能为空且符合URL，修改时可以为空，但如果不为空，要符合URL
	@URL(message = "logo地址必须是合法的url",groups = {AddVaildGroup.class,UpdateVaildGroup.class})
	@NotNull(message = "新增时logo地址不能为空",groups = {AddVaildGroup.class})
	private String logo;
	/**
	 * 介绍
	 */
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	@NotNull(groups = UpdateStatusVaildGroup.class,message = "修改状态状态码必须为1或2")
	@ListValue(vals = {0, 1}, groups = {UpdateStatusVaildGroup.class})
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@NotNull(groups = AddVaildGroup.class,message = "新增时检索首字母不能为空")
	@Pattern(regexp = "^[a-zA-Z]$",message = "检索首字母必须为a-zA-Z",groups = {AddVaildGroup.class, UpdateVaildGroup.class})
	private String firstLetter;
	/**
	 * 排序
	 */
	@NotNull(groups = AddVaildGroup.class,message = "新增时排序字段不能为空")
	@Min(value = 1,message = "排序必须大于等于1", groups = {AddVaildGroup.class ,UpdateVaildGroup.class})
	private Integer sort;

}
