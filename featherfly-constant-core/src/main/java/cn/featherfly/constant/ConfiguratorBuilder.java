package cn.featherfly.constant;

import java.util.ArrayList;
import java.util.Collection;

import cn.featherfly.common.lang.StringUtils;
import cn.featherfly.constant.parse.ConstantParser;
import cn.featherfly.conversion.core.ConversionPolicy;
import cn.featherfly.conversion.core.ConversionPolicys;
import cn.featherfly.conversion.parse.ClassParser;
import cn.featherfly.conversion.parse.JsonBeanPropertyParser;
import cn.featherfly.conversion.parse.JsonClassParser;
import cn.featherfly.conversion.parse.ParsePolity;

/**
 * <p>
 * 可配置常量配置对象装配器.
 * </p>
 * 
 * @author 钟冀
 */
public class ConfiguratorBuilder {

	// ********************************************************************
	// 构造方法
	// ********************************************************************
	
	private Collection<AbstractConfigurator> configurators;

	private ConversionPolicy conversionPolicy;
	
	private ParsePolity parsePolity;
	
	private String fileName;
	
	private ConstantPool constantPool;
	
	/**
	 * @param abstractConfigurators
	 *            abstractConfigurators
	 */
	public ConfiguratorBuilder(String fileName) {
		conversionPolicy = ConversionPolicys.getFormatConversionPolicy();
		parsePolity = new ParsePolity();
		parsePolity.register(new JsonClassParser());
		parsePolity.register(new JsonBeanPropertyParser());
		parsePolity.register(new ClassParser<>());
		parsePolity.register(new ConstantParser<>());
		configurators = new ArrayList<>();
		this.fileName = fileName;
	}
	
	public AbstractConfigurator build() {
		if (configurators.isEmpty()) {
			configurators.add(new DOMConfigurator(fileName, conversionPolicy, parsePolity));
			configurators.add(new YAMLConfigurator(fileName, conversionPolicy, parsePolity));
		}
		for (AbstractConfigurator abstractConfigurator : configurators) {
    		if (abstractConfigurator.match(StringUtils.substringAfterLast(fileName, "."))) {
    			return abstractConfigurator;
        	}
		}    	
    	throw new ConstantException("不支持的文件类型[" + fileName + "]，扩展名不正确");
	}

	/**
	 * 返回configurators
	 * @return configurators
	 */
	public Collection<AbstractConfigurator> getConfigurators() {
		return configurators;
	}

	/**
	 * 设置configurators
	 * @param configurators configurators
	 */
	public ConfiguratorBuilder setConfigurators(Collection<AbstractConfigurator> configurators) {
		this.configurators = configurators;
		return this;
	}

	/**
	 * 返回conversionPolicy
	 * @return conversionPolicy
	 */
	public ConversionPolicy getConversionPolicy() {
		return conversionPolicy;
	}

	/**
	 * 设置conversionPolicy
	 * @param conversionPolicy conversionPolicy
	 */
	public ConfiguratorBuilder setConversionPolicy(ConversionPolicy conversionPolicy) {
		this.conversionPolicy = conversionPolicy;
		return this;
	}

	/**
	 * 返回parsePolity
	 * @return parsePolity
	 */
	public ParsePolity getParsePolity() {
		return parsePolity;
	}

	/**
	 * 设置parsePolity
	 * @param parsePolity parsePolity
	 */
	public ConfiguratorBuilder setParsePolity(ParsePolity parsePolity) {
		this.parsePolity = parsePolity;
		return this;
	}

	/**
	 * 返回fileName
	 * @return fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * 设置fileName
	 * @param fileName fileName
	 */
	public ConfiguratorBuilder setFileName(String fileName) {
		this.fileName = fileName;
		return this;
	}
}
