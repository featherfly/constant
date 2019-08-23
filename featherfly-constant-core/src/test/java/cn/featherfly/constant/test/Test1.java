
package cn.featherfly.constant.test;

import org.testng.annotations.Test;
import org.yufei.User;

import cn.featherfly.constant.ConstantConfigurator;
import cn.featherfly.constant.ConstantPool;
import cn.featherfly.constant.configuration.ConstantParameter;
import cn.featherfly.constant.description.ConstantClassDescription;
import cn.featherfly.constant.description.ConstantDescription;

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
public class Test1 {

    @Test
    public void test() {

    }

    public static void main(String[] args) {
        ConstantConfigurator.config("ApplicationConstant.xml");
        ConstantPool pool = ConstantPool.getDefault();

        System.out.println(
                pool.getConstant(ConstantParameter.class).getBasePackeges());
        ConstantTest ct = pool.getConstant(ConstantTest.class);
        System.out.println(ct.getName());
        System.out.println(ct.getAge());
        System.out.println(ct.isIs());

        ConstantClassDescription constantClassDescription = pool
                .getConstantDescription(ConstantTest.class);
        System.out.println(constantClassDescription.getName() + " -> "
                + constantClassDescription.getDescp());
        for (ConstantDescription constantDescription : constantClassDescription
                .getConstantDescriptions()) {
            System.out.println("\t" + constantDescription.getDescp() + " -> "
                    + constantDescription.getName() + " : "
                    + constantDescription.getValue());
        }

        User u = pool.getConstant(User.class);
        System.out.println(u.getName());

        for (ConstantClassDescription description : pool
                .getConstantDescriptions()) {
            System.out.println(
                    description.getName() + " -> " + description.getDescp()
                            + " : " + description.getConstantClass());
            for (ConstantDescription constantDescription : description
                    .getConstantDescriptions()) {
                System.out.println("\t" + constantDescription.getDescp()
                        + " -> " + constantDescription.getName() + " : "
                        + constantDescription.getValue());
            }
        }
    }
}
