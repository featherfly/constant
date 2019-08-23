package cn.featherfly.constant;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import cn.featherfly.common.bean.BeanDescriptor;
import cn.featherfly.common.bean.BeanProperty;
import cn.featherfly.common.bean.NoSuchPropertyException;
import cn.featherfly.constant.configuration.ConstantParameter;
import cn.featherfly.conversion.core.ConversionPolicy;
import cn.featherfly.conversion.parse.ParsePolity;

/**
 * <p>
 * YAML格式可配置常量配置读取.
 * </p>
 * 
 * @author 钟冀
 */
public class YAMLConfigurator extends AbstractConfigurator {

    private final ObjectMapper mapper;

    // ********************************************************************
    // 构造方法
    // ********************************************************************

    /**
     * @param fileName
     *            fileName
     * @param conversionPolicy
     *            conversionPolicy
     * @param parsePolity
     *            parsePolity
     */
    YAMLConfigurator(String fileName, ConversionPolicy conversionPolicy,
            ParsePolity parsePolity) {
        this(fileName, conversionPolicy, parsePolity, new ConstantPool());
    }

    /**
     * @param fileName
     *            fileName
     * @param conversionPolicy
     *            conversionPolicy
     * @param parsePolity
     *            parsePolity
     * @param constantPool
     *            constantPool
     */
    YAMLConfigurator(String fileName, ConversionPolicy conversionPolicy,
            ParsePolity parsePolity, ConstantPool constantPool) {
        super(fileName, conversionPolicy, parsePolity, constantPool);
        mapper = new ObjectMapper(new YAMLFactory());
    }

    // ********************************************************************
    // 方法
    // ********************************************************************

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Object> readCfg(URL cfgFile) {
        List<Object> constantList = new ArrayList<>();
        try {
            JsonNode jsonNode = mapper.readValue(cfgFile, JsonNode.class);
            Map<String, JsonNode> constantParameterMap = new HashMap<>();
            Map<String, JsonNode> constantMap = new HashMap<>();
            Iterator<Entry<String, JsonNode>> fieldsIter = jsonNode.fields();
            while (fieldsIter.hasNext()) {
                Entry<String, JsonNode> entry = fieldsIter.next();
                if (entry.getKey().contains(".")) {
                    constantMap.put(entry.getKey(), entry.getValue());
                } else {
                    constantParameterMap.put(entry.getKey(), entry.getValue());
                }
            }

            if (!constantParameterMap.isEmpty()) {
                Object constant = createConfigObject(ConstantParameter.DEFAULT,
                        constantParameterMap.entrySet().iterator());
                if (constant != null) {
                    constantList.add(constant);
                }
            }
            constantMap.forEach((k, v) -> {
                addConstant(k, v, constantList);
            });

        } catch (Exception e) {
            // logger.error("开始读取常量配置文件{}时发生错误：{}", cfgFile.getPath(),
            // e.getMessage());
            throw new ConstantException(e);
        }
        return constantList;
    }

    private void addConstant(String className, JsonNode jsonNode,
            List<Object> constantList) {
        Object constant = createConfigObject(className, jsonNode);
        if (constant != null) {
            constantList.add(constant);
        }
    }

    // ********************************************************************
    // private method
    // ********************************************************************

    // 从配置文件创建配置对象
    private Object createConfigObject(String className,
            JsonNode propertiesNode) {
        Object obj = initConstant(className);
        if (obj == null) {
            return null;
        }
        return createConfigObject(obj, propertiesNode.fields());
    }

    // 从配置文件创建配置对象
    private Object createConfigObject(Object obj,
            Iterator<Entry<String, JsonNode>> propertiesIter) {
        while (propertiesIter.hasNext()) {
            Entry<String, JsonNode> property = propertiesIter.next();
            String name = property.getKey();
            JsonNode value = property.getValue();
            if (value.isContainerNode()) {
                setProperty(obj, name, value);
            } else {
                setProperty(obj, name, value.asText());
            }
        }
        logger.debug("create constant {}", obj.getClass().getName());
        return obj;
    }

    private void setProperty(Object constant, String name, JsonNode value) {
        if (org.apache.commons.lang3.StringUtils.isBlank(name)) {
            throw new ConstantException("常量名为空");
        }
        if (value == null || org.apache.commons.lang3.StringUtils
                .isBlank(value.toString())) {
            throw new ConstantException("常量值为空");
        }
        try {
            BeanDescriptor<?> bd = BeanDescriptor
                    .getBeanDescriptor(constant.getClass());
            BeanProperty<?> property = bd.getBeanProperty(name);
            Type toType = property.getField().getGenericType();
            Object propertyValue = toObject(toType, value);
            property.setValueForce(constant, propertyValue);
        } catch (NoSuchPropertyException e) {
            throw new ConstantException(
                    String.format("没有在常量配置类%s中找到属性%s，请确认配置文件",
                            constant.getClass().getName(), name));
        } catch (Exception e) {
            throw new ConstantException(String.format(
                    "为常量配置类%s属性%s设置值%s时发生异常：%s", constant.getClass().getName(),
                    name, value, e.getMessage()));
        }
    }

    private <T> T toObject(Type toType, JsonNode value) throws IOException {
        if (toType instanceof ParameterizedType) {
            final Type parameterizedType = (toType);
            return mapper.readerFor(new TypeReference<T>() {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public Type getType() {
                    return parameterizedType;
                }
            }).readValue(value);
        } else {
            return mapper.readerFor((Class<?>) toType).readValue(value);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean match(String fileExtName) {
        return "yaml".equalsIgnoreCase(fileExtName);
    }

    // ********************************************************************
    // 属性
    // ********************************************************************

}
