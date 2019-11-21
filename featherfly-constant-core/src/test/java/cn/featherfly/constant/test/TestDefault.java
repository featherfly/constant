package cn.featherfly.constant.test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import javax.management.relation.Role;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import cn.featherfly.common.lang.ClassLoaderUtils;
import cn.featherfly.constant.ConstantConfigurator;
import cn.featherfly.constant.ConstantPool;

/**
 * @author 钟冀
 */
public class TestDefault {

    ConstantPool pool;

    @BeforeClass
    public void setUp() {
        ConstantConfigurator.config();
        pool = ConstantPool.getDefault();
    }

    @Test
    public void test() {
        ConstantTest constantTest = pool.getConstant(ConstantTest.class);

        System.err.println(ClassLoaderUtils.getResource(ConstantConfigurator.DEFAULT_FILE, this.getClass()));

        assertEquals(constantTest.getAge(), 18);
        assertEquals(constantTest.getName(), "yufei");
        assertEquals(constantTest.isIs(), false);

        System.err.println(constantTest);

        Role role = pool.getConstant(Role.class);
        assertNull(role);
    }
}
