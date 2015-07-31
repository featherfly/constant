package cn.featherfly.constant;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.type.classreading.MetadataReader;

import cn.featherfly.common.bean.BeanDescriptor;
import cn.featherfly.common.bean.BeanProperty;
import cn.featherfly.common.bean.BeanUtils;
import cn.featherfly.common.bean.NoSuchPropertyException;
import cn.featherfly.common.bean.matcher.BeanPropertyAnnotationMatcher;
import cn.featherfly.common.io.ClassPathScanningProvider;
import cn.featherfly.common.lang.ArrayUtils;
import cn.featherfly.common.lang.ClassLoaderUtils;
import cn.featherfly.common.lang.ClassUtils;
import cn.featherfly.common.lang.LangUtils;
import cn.featherfly.common.lang.StringUtils;
import cn.featherfly.constant.annotation.Constant;
import cn.featherfly.constant.annotation.ConstantClass;
import cn.featherfly.constant.configuration.ConstantParameter;
import cn.featherfly.constant.description.ConstantClassDescription;
import cn.featherfly.constant.description.ConstantDescription;
import cn.featherfly.conversion.core.BeanPropertyConversion;
import cn.featherfly.conversion.core.ConversionPolicy;
import cn.featherfly.conversion.parse.ParsePolity;
import cn.featherfly.conversion.parse.Parser;


/**
 * <p>
 * 可配置常量配置读取.
 * </p>
 * @author 钟冀
 */
public final class ConstantConfiguration {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ConstantConfiguration.class);    

    private static final String CLASS_NODE_CLASS = "class";
    private static final String CONSTANT_NODE_NAME = "name";
    private static final String CONSTANT_NODE_VALUE = "value";

    private final Map<String, String> parsedProperty = new HashMap<String, String>();
    
    private static final String DEFAULT_CONFIG_FILE_NAME = "ApplicationConstant.xml";

    private BeanPropertyConversion beanPropertyConversion;
    
    private ConstantParameter constantParameter;
    
//    private final Map<Class<?>, ConstantContent> constants = new HashMap<>();
    
    private final Map<Class<?>, Object> constants = new HashMap<Class<?>, Object>();

    private final Map<Class<?>, ConstantClassDescription> constantDescriptions =
            new HashMap<Class<?>, ConstantClassDescription>();

    boolean init;

    // ********************************************************************
    //    构造方法
    // ********************************************************************
    
    /**
     * @param conversionPolicy conversionPolicy
     */
    ConstantConfiguration(ConversionPolicy conversionPolicy) {
        this(null, conversionPolicy);
    }

    /**
     * @param metaSet metaSet
     * @param conversionPolicy conversionPolicy
     */
    ConstantConfiguration(Set<MetadataReader> metaSet, ConversionPolicy conversionPolicy) {
        this.metaSet = metaSet;
        if (conversionPolicy == null) {
            this.beanPropertyConversion = new BeanPropertyConversion();
        } else {
            this.beanPropertyConversion = new BeanPropertyConversion(conversionPolicy);
        }
        this.load();
    }

    // ********************************************************************
    //    方法
    // ********************************************************************
    
    /**
     * <p>
     * 获得指定类型的常量对象.
     * </p>
     * @param <T> 泛型
     * @param type 指定类型
     * @return 指定类型的常量对象
     */
    @SuppressWarnings("unchecked")
    public <T> T getConstant(Class<T> type) {
        return (T) constants.get(type);
    }

    /**
     * <p>
     * 判断是否已经存在指定的常量配置类.
     * </p>
     * @param type 常量配置类
     * @return 是否已经存在指定的常量配置类
     */
    public boolean hasConstant(Class<?> type) {
        return constants.containsKey(type);
    }

    /**
     * <p>
     * 返回常量对象集合.
     * </p>
     * @return 常量对象集合
     */
    public Collection<?> getConstants() {
        return constants.values();
    }
    
    /**
     * <p>
     * 获得指定类型的常量描述.
     * </p>
     * @param <T> 泛型
     * @param type 指定类型
     * @return 指定类型的常量描述
     */
    public <T> ConstantClassDescription getConstantDescription(Class<T> type) {
        return constantDescriptions.get(type);
    }

    /**
     * <p>
     * 返回常量描述集合.
     * </p>
     * @return 常量描述集合
     */
    public Collection<ConstantClassDescription> getConstantDescriptions() {
        return new ArrayList<ConstantClassDescription>(constantDescriptions.values());
    }
    
//    /**
//     * <p>
//     * 初始化.
//     * 使用默认配置文件[ApplicationConstant.xml]
//     * </p>
//     * @param metaSet 元数据
//     * @param conversionPolicy 数据转换策略
//     */
//    public static void init(Set<MetadataReader> metaSet, ConversionPolicy conversionPolicy) {
//        constantConfigurate = new ConstantConfigurate();
//        constantConfigurate.metaSet = metaSet;
//        if (conversionPolicy == null) {
//            constantConfigurate.beanPropertyConversion = new BeanPropertyConversion();
//        } else {
//            constantConfigurate.beanPropertyConversion = new BeanPropertyConversion(conversionPolicy);
//        }
//        constantConfigurate.load();
//    }

    // ********************************************************************
    //    private method
    // ********************************************************************

    // 初始化加载
    private void load() {
        if (!init) {
            List<Object> constantList = null;
            LOGGER.debug("开始初始化常量配置信息");
            try {
                // 读取配置文件信息
                constantList = loadConstantFromFile();
            } catch (Exception e) {
                throw new ConstantException("读取常量配置信息出错", e);
            }
            // 查找对象
            findComponents();
            // 延迟加载策略
            // 合并
            mergeConstantClass(constantList);
            // 设置为已经初始化
            init = true;                        
            LOGGER.debug("结束初始化常量配置信息");
        }
    }

    //加载配置文件 
    private List<Object> loadConstantFromFile() throws IOException {
        List<Object> constantList = new ArrayList<Object>();
        LOGGER.debug("开始读取常量配置文件");
        URL cfgFile = ClassLoaderUtils.getResource(DEFAULT_CONFIG_FILE_NAME, this.getClass());
        if (cfgFile == null) {
            LOGGER.debug("没有找到常量配置文件");
        } else {
            LOGGER.debug("找到常量配置文件：{}", cfgFile.getPath());
            constantList = readCfg(cfgFile);
        }
        LOGGER.debug("结束读取常量配置文件");
        return constantList;
    }

    //读取配置信息 2
    private List<Object> readCfg(URL cfgFile) {
        List<Object> constantList = new ArrayList<Object>();
        try {
            SAXReader reader = new SAXReader();
            Document doc = reader.read(cfgFile);
            Element root = doc.getRootElement();
            @SuppressWarnings("unchecked")
            List<Element> constantClassList = root.elements();
            
            // 设置本组件需要的配置信息
            initConfig(constantClassList);
            //
            for (Element constantClass : constantClassList) {
                Object constant = createConfigObject(constantClass);
                if (constant != null) {
                    constantList.add(constant);
                }
            }
        } catch (DocumentException e) {
            LOGGER.error("开始读取常量配置文件{}时发生错误：{}",
                    cfgFile.getPath(), e.getMessage());
        }
        return constantList;
    }

    // 初始化常量池
    private void findComponents() {
        LOGGER.debug("开始查找常量配置类");
        //初始化
//        ConstantPool.init();
        //加载配置类
        findComponents(basePackages);
        LOGGER.debug("结束查找常量配置类，共查找到{}个常量配置类，分别为{}",
                getConstants().size(), getConstants());
    }

    // 查找配置类
    private void findComponents(String[] basePackages) {
        if (metaSet == null) {
            // 如果没有提供metaSet，自己去扫描
            if (LangUtils.isEmpty(basePackages)) {
                throw new ConstantException("常量对象扫描的起始包[basePackage]未指定");
            }
            ClassPathScanningProvider provider = new ClassPathScanningProvider();
            metaSet = new HashSet<MetadataReader>();
            for (String bp : basePackages) {
                metaSet.addAll(provider.findMetadata(bp));
            }
        }
        for (MetadataReader metadataReader : metaSet) {
            String className = metadataReader.getClassMetadata().getClassName();
            try {
                if (metadataReader.getAnnotationMetadata()
                        .hasAnnotation(ConstantClass.class.getName())) {
                    Class<?> type = Class.forName(className);
                    if (hasConstant(type)) {
                        LOGGER.warn(String.format("重复的配置类%s,使用覆盖原则", className));
                    }
                    check(type);
                    addConstant(type.newInstance(), false);
                }
            } catch (ClassNotFoundException e) {
                throw new ConstantException(String.format("常量配置类%s没有找到", className));
            } catch (Exception e) {
                throw new ConstantException(String.format("常量配置类%s生成对象时发生异常：%s",
                        className, e.getMessage()));
            }
        }
    }

    //检查
    private void check(Class<?> type) {
        BeanDescriptor<?> bd = BeanDescriptor.getBeanDescriptor(type);
        String className = type.getName();
        for (BeanProperty<?> property : bd.getBeanProperties()) {
            String name = property.getName();
            if (property.isWritable()) {
                throw new ConstantException(String.format("常量配置类%s的属性%s不是只读，请去掉set方法",
                        className, name));
            }
            Constant constantAnnotation = property.getAnnotation(Constant.class);
            if (constantAnnotation == null) {
                throw new ConstantException(String.format("常量配置类%s的属性%s没有被@%s注解修饰",
                        className, name, Constant.class.getSimpleName()));
            } else if (StringUtils.isBlank(constantAnnotation.value())) {
                throw new ConstantException(String.format("常量配置类%s的属性%s注解@%s的描述为空",
                        className, name, Constant.class.getSimpleName()));
            }
        }
    }

    // 合并配置文件对象到常量池
    private void mergeConstantClass(Collection<?> constantList) {
        for (Object constant : constantList) {
            if (!hasConstant(constant.getClass())) {
                String basePackage = "";
                for (String bp : basePackages) {
                    basePackage += bp + ",";
                }
                throw new ConstantException(String.format("从%s开始查找到的常量配置类中没有找到从配置文件中读取的%s",
                        basePackage, constant.getClass().getName()));
            }
            addConstant(constant, true);
        }
        parse(constantList);
    }

    /**
     * <p>
     * 重新解析，用于延迟加载内容.
     * 由ConstantParameter.reParse配置是否启用
     * </p>
     */
    void reParse() {
        if (constantParameter.isReParse()) {
            parse(getConstants());
        }
    }
    
    /**
     * <p>
     * 延迟设置
     * </p>
     * @param constantList constantList
     */
    private void parse(Collection<?> constantList) {
        for (Object constant : constantList) {
            BeanDescriptor<?> bd = BeanDescriptor.getBeanDescriptor(constant.getClass());
            for (BeanProperty<?> property
                    : bd.findBeanPropertys(new BeanPropertyAnnotationMatcher(Constant.class))) {
                String bp = property.getOwnerType().getName() + "." + property.getName();
                String value = null;
                if (parsedProperty.containsKey(bp)) {
                    value = parsedProperty.get(bp);
                    // 延迟进行解析类设置
                    if (parsePolity != null && parsePolity.canParse(value)) {
                        LOGGER.trace("使用解析策略设置值 {}.{} -> {}"
                                , new Object[]{property.getOwnerType().getName()
                                        , property.getName(), value});
                        property.setValueForce(constant, parsePolity.parse(value, property));
                    }
                    // 延迟进行解析类设置
                }
            }
        }
    }

    //从配置文件创建配置对象 3
    private Object createConfigObject(Element constantClass) {
        Object obj = null;
        String className = constantClass.attributeValue(CLASS_NODE_CLASS);
        try {
            Class<?> type = Class.forName(className);
            BeanDescriptor<?> bd = BeanDescriptor.getBeanDescriptor(type);
            obj = type.newInstance();
            @SuppressWarnings("unchecked")
            List<Element> constantList = constantClass.elements();
            for (Element constant : constantList) {
                String name = constant.attributeValue(CONSTANT_NODE_NAME);
                String value = constant.attributeValue(CONSTANT_NODE_VALUE);
                if (StringUtils.isBlank(value)) {
                    value = constant.getTextTrim();
                }
                if (StringUtils.isBlank(name)) {
                    throw new ConstantException("常量名为空");
                }
                if (StringUtils.isBlank(value)) {
                    throw new ConstantException("常量值为空");
                }
                try {
                    BeanProperty<?> property = bd.getBeanProperty(name);
                    if (parsePolity != null && parsePolity.canParse(value)) {
                        LOGGER.trace("使用解析策略预加载  {}.{} -> {}"
                                , new Object[]{className, name, value});
                        //property.setValueForce(obj, parsePolity.parse(value));
                        parsedProperty.put(className + "." + name, value);
                    } else {
                        LOGGER.trace("使用转换器设置值 {}.{} -> {}"
                                , new Object[]{className, name, value});
                        property.setValueForce(obj,
                                beanPropertyConversion.toObject(value, property));
                    }
                } catch (NoSuchPropertyException e) {
                    throw new ConstantException(String.format("没有在常量配置类%s中找到属性%s，请确认配置文件",
                            className, name));
                } catch (Exception e) {
                    throw new ConstantException(String.format("为常量配置类%s属性%s设置值%s时发生异常：%s",
                        className, name, value, e.getMessage()));
                }
            }
        } catch (ClassNotFoundException e) {
            throw new ConstantException(String.format("常量配置类%s没有找到", className));
        } catch (Exception e) {
            throw new ConstantException(String.format("常量配置类%s生成对象时发生异常：%s", className, e.getMessage()));
        }
        return obj;
    }

    // 初始化Constant自己的配置信息
    // 这里才初始化各种组件
    private void initConfig(List<Element> constantClassList) {
        for (Element constantClass : constantClassList) {
            String className = constantClass.attributeValue(CLASS_NODE_CLASS);
            if (className.equals(ConstantParameter.class.getName())) {
                constantParameter = (ConstantParameter) createConfigObject(constantClass);
                
                // 设置basePackages
                if (LangUtils.isNotEmpty(constantParameter.getBasePackeges())) {
                    basePackages = constantParameter.getBasePackeges();
                }
                
                initParserPolity(constantParameter);
                return;
            }
        }
    }
    
    // 初始化parserPolity
    private void initParserPolity(ConstantParameter config) {
        if (parsePolity == null) {
            parsePolity = new ParsePolity();
            for (Class<?> parserClass : config.getParsers()) {
                if (!ClassUtils.isParent(Parser.class, parserClass)) {
                    throw new ConstantException(StringUtils.format(
                            "为[#1]的配置项parsers配置的参数[#2]不是[#3]的实现类"
                            , config.getClass().getName()
                            , parserClass.getName()
                            , Parser.class.getName()));
                } else {
                    parsePolity.register(
                            (Parser) BeanUtils.instantiateClass(parserClass));
                }
            }
        }
    }

    // 添加常量
    private void addConstant(Object constant, boolean onMerge) {
        BeanDescriptor<?> bd = BeanDescriptor.getBeanDescriptor(constant.getClass());

        ConstantClassDescription constantClassDescription = new ConstantClassDescription(
                constant.getClass().getName(), bd.getAnnotation(ConstantClass.class).value()
                , constant.getClass());

        for (BeanProperty<?> property
                : bd.findBeanPropertys(new BeanPropertyAnnotationMatcher(Constant.class))) {
            String bp = property.getOwnerType().getName() + "." + property.getName();
            String value = null;

            if (parsedProperty.containsKey(bp)) {
                value = parsedProperty.get(bp);
                // 延迟进行解析类设置
                if (onMerge) {
                    if (parsePolity != null && parsePolity.canParse(value)) {
                        LOGGER.trace("使用解析策略设置值 {}.{} -> {}"
                                , new Object[]{property.getOwnerType().getName()
                                        , property.getName(), value});
                        property.setValueForce(constant, parsePolity.parse(value, property));
                    }
                }
                // 延迟进行解析类设置
                LOGGER.trace("{}.{} -> {} 从策略缓存中读取设置"
                        , new Object[]{property.getOwnerType().getName(), property.getName()
                                , value});
            } else {
                Object v = property.getValue(constant);
                if (v != null) {
                    try {
                        value = beanPropertyConversion.toString(v, property);
                    } catch (Exception e) {
                        if (v.getClass().isArray()) {
                            value = ArrayUtils.toString(v);
                        } else if (v instanceof Collection || v instanceof Map) {
                            value = v.toString();
                        } else {
                            value = v.getClass().getName();
                        }
                    }
                }
            }
            ConstantDescription constantDescription = new ConstantDescription(
                    property.getName()
                    , property.getAnnotation(Constant.class).value()
                    , value
                    , constantClassDescription);
            constantClassDescription.addConstantDescription(constantDescription);
        }
        addConstant(constant, constantClassDescription);
    }
    
    /**
     * <p>
     * 添加常量对象到池中.
     * </p>
     * @param constant 常量对象
     * @param constantClassDescription 常量描述信息
     */
    private void addConstant(Object constant, ConstantClassDescription constantClassDescription) {
//        ConstantContent content = new ConstantContent(constant, constantClassDescription);
//        constants.put(constant.getClass(), content);
        if (constant != null) {
            constants.put(constant.getClass(), constant);
            constantDescriptions.put(constant.getClass(), constantClassDescription);
        }
    }

    // ********************************************************************
    //    属性
    // ********************************************************************

    private ParsePolity parsePolity;

    private Set<MetadataReader> metaSet;

    private String[] basePackages;

    /**
     * @return 返回basePackages
     */
    public String[] getBasePackages() {
        return basePackages;
    }

    /**
     * 返回parserPolity
     * @return parserPolity
     */
    public ParsePolity getParserPolity() {
        return parsePolity;
    }
}
