
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
public class User {

    private User() {
    }

    @Constant("名称")
    private String name = "default_name";

    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "User [name=" + name + "]";
    }

}
