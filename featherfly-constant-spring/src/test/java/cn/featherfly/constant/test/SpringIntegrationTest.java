package cn.featherfly.constant.test;

import static org.testng.Assert.assertTrue;

import java.util.Collection;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.yufei.Role;
import org.yufei.Roles;

import com.fasterxml.jackson.core.JsonProcessingException;

import cn.featherfly.constant.ConstantConfigurator;
import cn.featherfly.constant.ConstantPool;

/**
 * @author 钟冀
 */
public class SpringIntegrationTest extends TestBase {

    ConstantPool pool;
    ClassPathXmlApplicationContext context;

    @BeforeClass
    public void setUp() {
        ConstantConfigurator.config("AppConstant.xml");
        pool = ConstantPool.getDefault();
        context = new ClassPathXmlApplicationContext("application.xml");

    }

    @Test
    public void test() throws JsonProcessingException {
        Roles roles = context.getBean(Roles.class);
        Role role1 = roles.role;
        Role role2 = pool.getConstant(Role.class);
        System.out.println("context.getBean.role role.name -> " + role1.getName());
        System.out.println("pool.getConstant role.name -> " + role2.getName());
        assertTrue(role1.equals(role2));

        Collection<?> constants = pool.getConstants();
        for (Object constant : constants) {
            System.out.println(constant.getClass().getName());
            System.out.println(objectMapper.writerFor(constant.getClass()).writeValueAsString(constant));
        }
    }
}
