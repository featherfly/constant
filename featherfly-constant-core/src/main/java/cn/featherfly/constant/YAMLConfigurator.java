package cn.featherfly.constant;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

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
	 * @param fileName fileName
	 * @param conversionPolicy
	 *            conversionPolicy
	 * @param parsePolity
	 *            parsePolity
	 */
	YAMLConfigurator(String fileName, ConversionPolicy conversionPolicy, ParsePolity parsePolity) {
		this(fileName, conversionPolicy, parsePolity, new ConstantPool());
	}

	/**
	 * @param fileName fileName
	 * @param conversionPolicy
	 *            conversionPolicy
	 * @param parsePolity
	 *            parsePolity
	 * @param constantPool
	 *            constantPool
	 */
	YAMLConfigurator(String fileName, ConversionPolicy conversionPolicy, ParsePolity parsePolity, ConstantPool constantPool) {
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
		List<Object> constantList = new ArrayList<Object>();
		try {
			JsonNode jsonNode = mapper.readValue(cfgFile, JsonNode.class);
			Iterator<Entry<String, JsonNode>> fieldsIter = jsonNode.fields();
			while(fieldsIter.hasNext()) {
				Entry<String, JsonNode> entry = fieldsIter.next();
				Object constant = createConfigObject(entry.getKey(), entry.getValue());
				if (constant != null) {
					constantList.add(constant);
				}
				
			}
		} catch (Exception e) {
			logger.error("开始读取常量配置文件{}时发生错误：{}", cfgFile.getPath(), e.getMessage());
		}
		return constantList;
	}

	// ********************************************************************
	// private method
	// ********************************************************************

	// 从配置文件创建配置对象
	private Object createConfigObject(String className, JsonNode propertiesNode) {
		Object obj = null;
		try {
			Class<?> type = Class.forName(className);
			if (filter(type)) {
				logger.debug("filter type {}", type.getName());
				return null;
			}
			obj = type.newInstance();
			logger.debug("new instance for type {}", type.getName());
			
			Iterator<Entry<String, JsonNode>> propertiesIter = propertiesNode.fields();
			while(propertiesIter.hasNext()) {
				Entry<String, JsonNode> property = propertiesIter.next();				
				String name = property.getKey();
				String value = property.getValue().asText();
				setProperty(obj, name, value);
			}			
		} catch (ClassNotFoundException e) {
			throw new ConstantException(String.format("常量配置类%s没有找到", className));
		} catch (Exception e) {
			throw new ConstantException(String.format("常量配置类%s生成对象时发生异常：%s", className, e.getMessage()));
		}
		return obj;
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
