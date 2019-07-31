package cn.featherfly.constant;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.featherfly.common.bean.BeanDescriptor;
import cn.featherfly.common.bean.BeanProperty;
import cn.featherfly.common.bean.NoSuchPropertyException;
import cn.featherfly.common.bean.matcher.BeanPropertyAnnotationMatcher;
import cn.featherfly.common.lang.ArrayUtils;
import cn.featherfly.common.lang.ClassLoaderUtils;
import cn.featherfly.common.lang.LangUtils;
import cn.featherfly.common.lang.StringUtils;
import cn.featherfly.common.policy.WhiteBlackListPolicy;
import cn.featherfly.constant.annotation.Constant;
import cn.featherfly.constant.annotation.ConstantClass;
import cn.featherfly.constant.configuration.ConstantParameter;
import cn.featherfly.constant.description.ConstantClassDescription;
import cn.featherfly.constant.description.ConstantDescription;
import cn.featherfly.conversion.core.BeanPropertyConversion;
import cn.featherfly.conversion.core.ConversionPolicy;
import cn.featherfly.conversion.parse.ParsePolity;


/**
 * <p>
 * AbstractConfigurator.
 * </p>
 * @author 钟冀
 */
public abstract class AbstractConfigurator {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
        
    private final BeanPropertyStore parsedProperty = new BeanPropertyStore();    

    private BeanPropertyConversion beanPropertyConversion;
          
    private ParsePolity parsePolity;

    private ConstantPool constantPool;
    
    private String fileName;
    
    private WhiteBlackListPolicy<Class<?>> filterTypePolicy;
        
    // ********************************************************************
    //    构造方法
    // ********************************************************************
    
    /**
     * @param fileName fileName
     * @param conversionPolicy conversionPolicy
     * @param parsePolity parsePolity
     */
    AbstractConfigurator(String fileName, ConversionPolicy conversionPolicy, ParsePolity parsePolity, ConstantPool constantPool) {    	
        if (conversionPolicy == null) {
            this.beanPropertyConversion = new BeanPropertyConversion();
        } else {
            this.beanPropertyConversion = new BeanPropertyConversion(conversionPolicy);
        }
        this.parsePolity = parsePolity;
        this.constantPool = constantPool;
        this.fileName = fileName;
        filterTypePolicy = new WhiteBlackListPolicy<Class<?>>() {
			@Override
			protected boolean isEquals(Class<?> target1, Class<?> target2) {
				return target1 == target2;
			}
		};
		filterTypePolicy.addWhite(ConstantParameter.class);
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
    public <T> T getConstant(Class<T> type) {
        return constantPool.getConstant(type);
    }

    /**
     * <p>
     * 判断是否已经存在指定的常量配置类.
     * </p>
     * @param type 常量配置类
     * @return 是否已经存在指定的常量配置类
     */
    public boolean hasConstant(Class<?> type) {
        return constantPool.hasConstant(type);
    }

    /**
     * <p>
     * 返回常量对象集合.
     * </p>
     * @return 常量对象集合
     */
    public Collection<?> getConstants() {
        return constantPool.getConstants();
    }
    
    /**
     * <p>
     * 获得指定类型的常量描述.
     * </p>
     * @param type 指定类型
     * @return 指定类型的常量描述
     */
	public ConstantClassDescription getConstantDescription(Class<?> type) {
        return constantPool.getConstantDescription(type);
    }

    /**
     * <p>
     * 返回常量描述集合.
     * </p>
     * @return 常量描述集合
     */
    public Collection<ConstantClassDescription> getConstantDescriptions() {
        return constantPool.getConstantDescriptions();
    }
    
    /**
     * <p>
     * 从配置文件读取常量对象集合
     * </p>
     * @param cfgFile 配置文件
     * @return 常量对象集合
     */
    protected abstract Collection<Object> readCfg(URL cfgFile);
    
    /**
     * <p>
     * 是否过滤指定类型
     * </p>
     * @param type type
     * @return type
     */
    protected boolean filter(Class<?> type) {
    	return filterTypePolicy.isAllow(type);
    }
    
    /**
     * <p>
     * 为常量赋予配置文件中的值
     * </p>
     * @param constant
     * @param name
     * @param value
     */
    protected void setProperty(Object constant, String name, String value) {
    	if (StringUtils.isBlank(name)) {
            throw new ConstantException("常量名为空");
        }
        if (StringUtils.isBlank(value)) {
            throw new ConstantException("常量值为空");
        }
        String className = constant.getClass().getName();
        try {
        	BeanDescriptor<?> bd = BeanDescriptor.getBeanDescriptor(constant.getClass());
            BeanProperty<?> property = bd.getBeanProperty(name);
            if (parsePolity != null && parsePolity.canParse(value)) {
                logger.trace("使用解析策略预加载  {}.{} -> {}"
                        , new Object[]{className, name, value});
                //property.setValueForce(obj, parsePolity.parse(value));
//                parsedProperty.put(className + "." + name, value);                
                parsedProperty.put(className, name, value);
            } else {
                logger.trace("使用转换器设置值 {}.{} -> {}"
                        , new Object[]{className, name, value});
                property.setValueForce(constant,
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
    

    // ********************************************************************
    //    private method
    // ********************************************************************
    

    // 初始化加载
    protected void load() {
    	if (!match(StringUtils.substringAfterLast(fileName, "."))) {
    		throw new ConstantException("不支持的文件类型[" + fileName + "]，扩展名不正确");
    	}
        Collection<Object> constantList = null;
        logger.debug("开始从{}初始化常量配置信息", fileName);
        try {
            // 读取配置文件信息
            constantList = loadConstantsFromFile(fileName);
            // 合并
            mergeConstantClass(constantList);
        } catch (Exception e) {
            throw new ConstantException("从" + fileName + "读取常量配置信息出错", e);
        }
        logger.debug("结束从{}初始化常量配置信息", fileName);
    }
    
    /**
     * <p>
     * 从配置文件读取常量对象集合
     * </p>
     * @param fileName 配置文件
     * @return 常量对象集合
     */
	private Collection<Object> loadConstantsFromFile(String fileName) {
		Collection<Object> constantList = new ArrayList<Object>();
        logger.debug("开始从{}读取常量配置文件", fileName);
        URL cfgFile = ClassLoaderUtils.getResource(fileName, this.getClass());
        if (cfgFile == null) {
            logger.debug("没有找到常量配置文件");
        } else {
            logger.debug("找到常量配置文件：{}", cfgFile.getPath());
            constantList = readCfg(cfgFile);
        }
        logger.debug("结束从{}读取常量配置文件", fileName);
        return constantList;
	}
    
    // 合并配置文件对象到常量池
    private void mergeConstantClass(Collection<?> constantList) {
        for (Object constant : constantList) {
            if (!hasConstant(constant.getClass())) {
                throw new ConstantException(String.format("从%s存储的常量配置类中没有找到从配置文件中读取的%s",
                        constantPool.toString(), constant.getClass().getName()));
            }
            addConstant(constant, true);
        }
        parse(constantList);
    }
    
    /**
     * <p>
     * 延迟设置
     * </p>
     * @param constantList constantList
     */
    protected void parse(Collection<?> constantList) {
//    	System.err.println(parsedProperty);
    	if (LangUtils.isEmpty(parsedProperty)) {
    		return;
    	}
        for (Object constant : constantList) {
        	String className = constant.getClass().getName(); 
//        	System.err.println(constant.getClass());
        	if (!parsedProperty.hasClass(className)) {
        		continue;
        	}
            BeanDescriptor<?> bd = BeanDescriptor.getBeanDescriptor(constant.getClass());
            for (BeanProperty<?> property
                    : bd.findBeanPropertys(new BeanPropertyAnnotationMatcher(Constant.class))) {
                String value = null;                
                if (parsedProperty.hasProperty(className, property.getName())) {
                    value = parsedProperty.get(className, property.getName());
                    // 延迟进行解析类设置
                    if (parsePolity != null && parsePolity.canParse(value)) {
                        logger.trace("使用解析策略设置值 {}.{} -> {}"
                                , new Object[]{property.getOwnerType().getName()
                                        , property.getName(), value});
                        property.setValueForce(constant, parsePolity.parse(value, property));
                    }
                    // 延迟进行解析类设置
                }
            }
        }
    }
        
    // 添加常量
    protected void addConstant(Object constant, boolean onMerge) {
    	if (!onMerge) {
    		check(constant.getClass());
    	}
    	
        BeanDescriptor<?> bd = BeanDescriptor.getBeanDescriptor(constant.getClass());
        ConstantClassDescription constantClassDescription = new ConstantClassDescription(
                constant.getClass().getName(), bd.getAnnotation(ConstantClass.class).value()
                , constant.getClass());

        for (BeanProperty<?> property
                : bd.findBeanPropertys(new BeanPropertyAnnotationMatcher(Constant.class))) {
            String value = null;

            if (parsedProperty.hasProperty(property.getOwnerType().getName(), property.getName())) {
                value = parsedProperty.get(property.getOwnerType().getName(), property.getName());
                // 延迟进行解析类设置
                if (onMerge) {
                    if (parsePolity != null && parsePolity.canParse(value)) {
                        logger.trace("使用解析策略设置值 {}.{} -> {}"
                                , new Object[]{property.getOwnerType().getName()
                                        , property.getName(), value});
                        property.setValueForce(constant, parsePolity.parse(value, property));
                    }
                }
                // 延迟进行解析类设置
                logger.trace("{}.{} -> {} 从策略缓存中读取设置"
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
        constantPool.addConstant(constant, constantClassDescription);
    }
    
    /**
	 * <p>
	 * 检查常量类型是否符合规范
	 * </p>
	 * @param constantType constantType
	 * @throws ConstantException
	 */
    protected void check(Class<?> constantType) {
        BeanDescriptor<?> bd = BeanDescriptor.getBeanDescriptor(constantType);
        String className = constantType.getName();
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
    
    protected abstract boolean match(String fileExtName);
    
    private static class BeanPropertyStore {
    	
    	Map<String, PropertyStore> beanProperties;
    	
    	private BeanPropertyStore() {    		
    		beanProperties = new HashMap<>(); 
    	}
    	
    	public BeanPropertyStore put(String className, String propertyName, String propertyValue) {
    		PropertyStore propertyStore = beanProperties.get(className);
    		if (propertyStore == null) {
    			propertyStore = new PropertyStore();
    			beanProperties.put(className, propertyStore);
    		}
    		propertyStore.put(propertyName, propertyValue);
    		return this;
    	}
    	
    	public PropertyStore get(String className) {
    		return beanProperties.get(className);
    	}
    	
    	public String get(String className, String propertyName) {
    		PropertyStore propertyStore = get(className);
    		if (propertyStore != null) {
    			return propertyStore.get(propertyName);
    		}
    		return null;
    	}
    	
    	public boolean hasClass(String className) {
    		return beanProperties.containsKey(className);
    	}
    	
    	public boolean hasProperty(String className, String propertyName) {
    		PropertyStore propertyStore = get(className);
    		if (propertyStore != null) {
    			return propertyStore.hasProperty(propertyName);
    		}
    		return false;
    	}
    	
    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
    	public String toString() {
    		return beanProperties.toString();
    	}
    }
    
    private static class PropertyStore {    	
    	    	
    	Map<String, String> beanProperties;
    	
    	private PropertyStore() {
    		beanProperties = new HashMap<>(); 
    	}
    	
    	public PropertyStore put(String name, String value) {
    		beanProperties.put(name, value);
    		return this;
    	}
    	
    	public String get(String name) {
    		return beanProperties.get(name);
    	}
    	
    	public boolean hasProperty(String name) {
    		return beanProperties.containsKey(name);
    	}
    	
    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
    	public String toString() {
    		return beanProperties.toString();
    	}
    }
    
    // ********************************************************************
    //    属性
    // ********************************************************************

	/**
	 * 返回constantPool
	 * @return constantPool
	 */
	protected ConstantPool getConstantPool() {
		return constantPool;
	}

	/**
	 * 返回fileName
	 * @return fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * 返回whiteBlackListPolicy
	 * @return whiteBlackListPolicy
	 */
	public WhiteBlackListPolicy<Class<?>> getFilterTypePolicy() {
		return filterTypePolicy;
	}
}
