package cn.featherfly.constant.test;

import static org.testng.Assert.assertTrue;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.yufei.Role;
import org.yufei.Roles;

import cn.featherfly.constant.ConstantConfigurator;
import cn.featherfly.constant.ConstantPool;

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
public class SpringIntegrationTest {

    ConstantPool pool;
    ClassPathXmlApplicationContext context;

    @BeforeClass
    public void setUp() {
        ConstantConfigurator.config("AppConstant.xml");
        pool = ConstantPool.getDefault();
        context = new ClassPathXmlApplicationContext("application.xml");

    }

    @Test
    public void test() {
        Roles roles = context.getBean(Roles.class);
        Role role1 = roles.role;
        Role role2 = pool.getConstant(Role.class);
        System.out.println(
                "context.getBean.role role.name -> " + role1.getName());
        System.out.println("pool.getConstant role.name -> " + role2.getName());
        assertTrue(role1.equals(role2));
    }
}
