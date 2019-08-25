
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
import cn.featherfly.conversion.core.ConversionPolicys;

/**
 * <p>
 * Main 类的说明放这里
 * </p>
 * 
 * @author 钟冀
 */
public class MulitiConfigFileTest {

    ObjectMapper objectMapper;

    /**
     * 
     */
    public MulitiConfigFileTest() {
    }

    @BeforeClass
    void before() {
        DOMConfigurator.configure(ClassLoaderUtils.getResource("log4j.xml",
                MulitiConfigFileTest.class));
        objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    @Test
    public void test1() throws JsonProcessingException {
        ConstantConfigurator configurator = ConstantConfigurator
                .config(ConversionPolicys.getFormatConversionPolicy());
        ConstantParameter p = ConstantPool.getDefault()
                .getConstant(ConstantParameter.class);
        System.out.println(ArrayUtils.toString(p.getBasePackeges()));
        System.out.println(ArrayUtils.toString(p.getConfigFiles()));
        Collection<?> constants = configurator.getConstants();
        for (Object constant : constants) {
            System.out.println(constant.getClass().getName());
            System.out.println(objectMapper.writerFor(constant.getClass())
                    .writeValueAsString(constant));
        }
    }

    @Test
    public void test2() throws JsonProcessingException {
        ConstantConfigurator configurator = ConstantConfigurator
                .config("constant2.yaml");
        ConstantParameter p = ConstantPool.getDefault()
                .getConstant(ConstantParameter.class);
        System.out.println(ArrayUtils.toString(p.getBasePackeges()));
        System.out.println(ArrayUtils.toString(p.getConfigFiles()));
        Collection<?> constants = configurator.getConstants();
        for (Object constant : constants) {
            System.out.println(constant.getClass().getName());
            System.out.println(objectMapper.writerFor(constant.getClass())
                    .writeValueAsString(constant));
        }
    }

    @Test
    public void test3() throws JsonProcessingException {
        ConstantConfigurator configurator = ConstantConfigurator
                .config("ApplicationConstant.xml");
        ConstantParameter p = ConstantPool.getDefault()
                .getConstant(ConstantParameter.class);
        System.out.println(ArrayUtils.toString(p.getBasePackeges()));
        System.out.println(ArrayUtils.toString(p.getConfigFiles()));
        Collection<?> constants = configurator.getConstants();
        for (Object constant : constants) {
            System.out.println(constant.getClass().getName());
            System.out.println(objectMapper.writerFor(constant.getClass())
                    .writeValueAsString(constant));
        }
    }

    public static void main(String[] args) throws Exception {

        // URL url = ClassLoaderUtils.getResource("Constant.yaml",
        // YAMLConfigurator.class);
        //
        // final YAMLFactory yamlFactory = new YAMLFactory();
        // final ObjectMapper mapper = new ObjectMapper(yamlFactory);
        // mapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        //
        // JsonNode jsonNode = mapper.readValue(url, JsonNode.class);
        // System.out.println("-------------");
        // Iterator<Entry<String, JsonNode>> fieldsIter = jsonNode.fields();
        // while(fieldsIter.hasNext()) {
        // Entry<String, JsonNode> entry = fieldsIter.next();
        // System.out.println(entry.getKey());
        // Iterator<Entry<String, JsonNode>> propertiesIter =
        // entry.getValue().fields();
        // while(propertiesIter.hasNext()) {
        // Entry<String, JsonNode> property = propertiesIter.next();
        // System.out.println(property.getKey());
        // System.out.println(property.getValue().asText());
        // }
        // }
    }
}
