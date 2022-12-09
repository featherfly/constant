
package cn.featherfly.constant.test;

import java.util.Collection;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import cn.featherfly.common.lang.ArrayUtils;
import cn.featherfly.constant.ConstantConfigurator;
import cn.featherfly.constant.ConstantPool;
import cn.featherfly.constant.configuration.ConstantParameter;

/**
 * <p>
 * Main 类的说明放这里
 * </p>
 *
 * @author 钟冀
 */
public class NoFileTest extends TestBase {

    /**
     *
     */
    public NoFileTest() {
    }

    @BeforeClass
    void before() {
    }

    @Test
    public void test1() throws JsonProcessingException {
        ConstantConfigurator configurator = ConstantConfigurator.config();
        ConstantParameter p = ConstantPool.getDefault().getConstant(ConstantParameter.class);
        System.out.println(ArrayUtils.toString(p.getBasePackeges()));
        System.out.println(ArrayUtils.toString(p.getConfigFiles()));
        Collection<?> constants = configurator.getConstants();
        for (Object constant : constants) {
            System.out.println(constant.getClass().getName());
            System.out.println(objectMapper.writerFor(constant.getClass()).writeValueAsString(constant));
        }
    }

    @Test
    public void test2() throws JsonProcessingException {
        ConstantConfigurator configurator = ConstantConfigurator.config("constant3.yaml");
        ConstantParameter p = ConstantPool.getDefault().getConstant(ConstantParameter.class);
        System.out.println(ArrayUtils.toString(p.getBasePackeges()));
        System.out.println(ArrayUtils.toString(p.getConfigFiles()));
        Collection<?> constants = configurator.getConstants();
        for (Object constant : constants) {
            System.out.println(constant.getClass().getName());
            System.out.println(objectMapper.writerFor(constant.getClass()).writeValueAsString(constant));
        }
    }
}
