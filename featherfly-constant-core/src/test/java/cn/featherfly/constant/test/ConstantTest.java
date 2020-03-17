
package cn.featherfly.constant.test;

import java.util.Arrays;

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
@ConstantClass("测试")
public class ConstantTest {

    private ConstantTest() {
    }

    @Constant("名称")
    private String name = "yufei";
    @Constant("年龄")
    private int age = 18;
    @Constant("是否")
    private boolean is;
    @Constant("数字类型")
    private Class<?>[] numberTypes = { Integer.class, Long.class, Float.class, Double.class };

    public Class<?>[] getNumberTypes() {
        return numberTypes;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public boolean isIs() {
        return is;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "ConstantTest [name=" + name + ", age=" + age + ", is=" + is + ", numberTypes="
                + Arrays.toString(numberTypes) + "]";
    }

}
