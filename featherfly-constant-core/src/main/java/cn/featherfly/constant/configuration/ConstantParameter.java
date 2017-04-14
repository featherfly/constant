
package cn.featherfly.constant.configuration;

import cn.featherfly.constant.annotation.Constant;
import cn.featherfly.constant.annotation.ConstantClass;
import cn.featherfly.constant.parse.ConstantParser;
import cn.featherfly.conversion.parse.ClassParser;
import cn.featherfly.conversion.parse.JsonBeanPropertyParser;

/**
 * <p>
 * 可配置常量组件参数.
 * </p>
 * @author 钟冀
 */
@ConstantClass("可配置常量组件")
public class ConstantParameter {
    
    /**
     *
     */
    public ConstantParameter() {
    }

    @Constant("开始扫描配置类的起始包")
    private String[] basePackeges;

    @Constant("解析字符串时的解析器")
    private Class<?>[] parsers = new Class<?>[]{ClassParser.class
        , ConstantParser.class, JsonBeanPropertyParser.class};
                
    @Constant("用户配置文件")
    private String[] configFiles = new String[]{"ApplicationConstant.xml"};
    
    @Constant("是否重新解析，当有解析器需要用到初始化比constant晚的内容时，可以打开此属性")
    private boolean reParse;

    @Constant("是否开发模式")
    private boolean devMode;

    /**
     * 返回开始扫描配置类的包.
     * @return basePackeges
     */
    public String[] getBasePackeges() {
        return basePackeges;
    }

    /**
     * 返回解析字符串时的解析器
     * @return parsers
     */
    public Class<?>[] getParsers() {
        return parsers;
    }

    /**
     * 返回是否重新解析，当有解析器需要用到初始化比constant晚的内容时，可以打开此属性
     * @return reParse
     */
    public boolean isReParse() {
        return reParse;
    }

    /**
     * 返回devMode
     * @return devMode
     */
    public boolean isDevMode() {
        return devMode;
    }

	/**
	 * 返回configFiles
	 * @return configFiles
	 */
	public String[] getConfigFiles() {
		return configFiles;
	}
}
