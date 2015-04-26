
package cn.featherfly.constant;

import java.util.Collection;

import cn.featherfly.constant.description.ConstantClassDescription;
import cn.featherfly.conversion.core.ConversionPolicy;
/**
 * <p>
 * 常量处理器.
 * </p>
 * @author 钟冀
 */
public final class ConstantPool{

    private static ConstantPool constantPool;
    
    private ConstantConfiguration configuration;

    /**
     * 构造方法
     * @param configuration ConstantConfiguration
     */
    private ConstantPool(ConstantConfiguration configuration) {
        this.configuration = configuration;
    }
    /**
     * <p>
     * 获取默认ConstantPool
     * </p>
     * @return ConstantPool
     */
    public static ConstantPool getDefault() {
        checkInit();
        return constantPool;
    }
    
    /**
     * <p>
     * 初始化默认池.
     * </p>
     */
    public static void init() {
        init(null);
    }

    /**
     * <p>
     * 初始化默认常良池.
     * @param conversionPolicy 转换策略
     * </p>
     */
    public static void init(ConversionPolicy conversionPolicy) {
        if (constantPool == null) {
            synchronized (ConstantPool.class) {
                if (constantPool == null) {
                    constantPool = new ConstantPool(new ConstantConfiguration(conversionPolicy));
                    constantPool.configuration.reParse();
                }
            }
        }
    }

    /**
     * <p>
     * 获得指定类型的常量对象.
     * </p>
     * @param <T> 泛型
     * @param type 指定类型
     * @return 指定类型的常量对象
     */
    public <T> T getConstant(Class<T> type) {
        return configuration.getConstant(type);
    }

    /**
     * <p>
     * 判断是否已经存在指定的常量配置类.
     * </p>
     * @param type 常量配置类
     * @return 是否已经存在指定的常量配置类
     */
    public boolean hasConstant(Class<?> type) {
        return configuration.hasConstant(type);
    }

    /**
     * <p>
     * 返回常量对象集合.
     * </p>
     * @return 常量对象集合
     */
    public Collection<?> getConstants() {
        return configuration.getConstants();
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
        return configuration.getConstantDescription(type);
    }

    /**
     * <p>
     * 返回常量描述集合.
     * </p>
     * @return 常量描述集合
     */
    public Collection<ConstantClassDescription> getConstantDescriptions() {
        return configuration.getConstantDescriptions();
    }
    /**
     * <p>
     * 返回是否初始化
     * </p>
     * @return 是否初始化
     */
    public static boolean isInit() {
        return constantPool != null;
    }

    // ********************************************************************
    // private method
    // ********************************************************************

    //检查是否初始化
    private static void checkInit() {
        if (!isInit()) {
            throw new ConstantException("常量池未初始化");
        }
    }
}
