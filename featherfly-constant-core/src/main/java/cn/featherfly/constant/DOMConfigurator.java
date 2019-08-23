package cn.featherfly.constant;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import cn.featherfly.conversion.core.ConversionPolicy;
import cn.featherfly.conversion.parse.ParsePolity;

/**
 * <p>
 * XML格式可配置常量配置读取.
 * </p>
 *
 * @author 钟冀
 */
public class DOMConfigurator extends AbstractConfigurator {

    private static final String CLASS_NODE_CLASS = "class";
    private static final String CONSTANT_NODE_NAME = "name";
    private static final String CONSTANT_NODE_VALUE = "value";

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
    DOMConfigurator(String fileName, ConversionPolicy conversionPolicy,
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
    DOMConfigurator(String fileName, ConversionPolicy conversionPolicy,
            ParsePolity parsePolity, ConstantPool constantPool) {
        super(fileName, conversionPolicy, parsePolity, constantPool);
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
            SAXReader reader = new SAXReader();
            Document doc = reader.read(cfgFile);
            Element root = doc.getRootElement();
            List<Element> constantClassList = root.elements();
            for (Element constantClass : constantClassList) {
                Object constant = createConfigObject(constantClass);
                if (constant != null) {
                    constantList.add(constant);
                }
            }
        } catch (DocumentException e) {
            logger.error("开始读取常量配置文件{}时发生错误：{}", cfgFile.getPath(),
                    e.getMessage());
        }
        return constantList;
    }

    // ********************************************************************
    // private method
    // ********************************************************************

    // 从配置文件创建配置对象
    private Object createConfigObject(Element constantClass) {
        String className = constantClass.attributeValue(CLASS_NODE_CLASS);
        if (className == null) {
            className = constantClass.getName();
        }
        Object obj = initConstant(className);
        if (obj == null) {
            return null;
        }
        List<Element> constantList = constantClass.elements();
        for (Element constant : constantList) {
            String name = constant.attributeValue(CONSTANT_NODE_NAME);
            if (name == null) {
                name = constant.getName();
            }
            String value = constant.attributeValue(CONSTANT_NODE_VALUE);
            if (org.apache.commons.lang3.StringUtils.isBlank(value)) {
                value = constant.getTextTrim();
            }
            setProperty(obj, name, value);
        }
        return obj;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean match(String fileExtName) {
        return "xml".equalsIgnoreCase(fileExtName);
    }

    // ********************************************************************
    // 属性
    // ********************************************************************

}
