
package org.yufei;

import cn.featherfly.constant.annotation.Constant;
import cn.featherfly.constant.annotation.ConstantClass;

/**
 * <p>
 * 类的说明放这里
 * </p>
 * <p>
 * copyright featherfly 2010-2020, all rights reserved.
 * </p>
 *
 * @author 钟冀
 */
@ConstantClass("用户")
public class User2 {
	@Constant("名称")
	private String name = "default_user2_name";
	
	@Constant("年龄")
	private Integer age = 15;

	public String getName() {
		return name;
	}

	/**
	 * 返回age
	 * @return age
	 */
	public Integer getAge() {
		return age;
	}
	
	
}
