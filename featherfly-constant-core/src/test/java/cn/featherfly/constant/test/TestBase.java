
package cn.featherfly.constant.test;

import org.apache.log4j.xml.DOMConfigurator;
import org.testng.annotations.BeforeClass;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import cn.featherfly.common.lang.ClassLoaderUtils;

/**
 * <p>
 * Main 类的说明放这里
 * </p>
 *
 * @author 钟冀
 */
public class TestBase {

    ObjectMapper objectMapper;

    /**
     *
     */
    public TestBase() {
    }

    @BeforeClass
    void beforeClass() {
        System.out.println(ClassLoaderUtils.getResource("log4j.xml", this.getClass()));
        //        DOMConfigurator.configure("log4j.xml");
        DOMConfigurator.configure(ClassLoaderUtils.getResource("log4j.xml", this.getClass()));
        objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }
}
