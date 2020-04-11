package cn.featherfly.constant;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.featherfly.conversion.parse.ParsePolity;
import cn.featherfly.conversion.string.ToStringConversionPolicy;

/**
 * <p>
 * 默认可配置常量配置读取.
 * </p>
 *
 * @author 钟冀
 */
public class DefaultConfigurator extends AbstractConfigurator {

    // ********************************************************************
    // 构造方法
    // ********************************************************************

    /**
     * @param file             file
     * @param conversionPolicy conversionPolicy
     * @param parsePolity      parsePolity
     * @param constantPool     constantPool
     */
    DefaultConfigurator(ToStringConversionPolicy conversionPolicy, ParsePolity parsePolity, ConstantPool constantPool) {
        super(null, conversionPolicy, parsePolity, constantPool);
    }

    // ********************************************************************
    // 方法
    // ********************************************************************

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Object> readCfg(URL cfgFile) {
        return new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean match(String fileExtName) {
        return true;
    }

    // ********************************************************************
    // 属性
    // ********************************************************************

}
