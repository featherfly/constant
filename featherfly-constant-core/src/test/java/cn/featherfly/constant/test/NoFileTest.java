
package cn.featherfly.constant.test;

import java.util.Collection;

import org.apache.log4j.xml.DOMConfigurator;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import cn.featherfly.common.lang.ArrayUtils;
import cn.featherfly.common.lang.ClassLoaderUtils;
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
public class NoFileTest {

    ObjectMapper objectMapper;

    /**
     *
     */
    public NoFileTest() {
    }

    @BeforeClass
    void before() {
        DOMConfigurator.configure(ClassLoaderUtils.getResource("log4j.xml", NoFileTest.class));
        objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
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
