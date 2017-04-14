
package cn.featherfly.constant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import cn.featherfly.constant.description.ConstantClassDescription;
/**
 * <p>
 * 常量处理器.
 * </p>
 * @author 钟冀
 */
public final class ConstantPool{

    private static ConstantPool constantPool;
    
    private final Map<Class<?>, Object> constants = new HashMap<>();

    private final Map<Class<?>, ConstantClassDescription> constantDescriptions =
            new HashMap<Class<?>, ConstantClassDescription>();
    
    /**
     * 构造方法
     * @param configuration ConstantConfiguration
     */
    ConstantPool() {
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
    static ConstantPool init() {
    	if (constantPool == null) {
            synchronized (ConstantPool.class) {
                if (constantPool == null) {
                	constantPool = new ConstantPool();
                }
            }
        }
    	return constantPool;
    }
    
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
     * @param type 指定类型
     * @return 指定类型的常量描述
     */
    public ConstantClassDescription getConstantDescription(Class<?> type) {
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
        
    /**
     * <p>
     * 添加常量对象到池中.
     * </p>
     * @param constant 常量对象
     * @param constantClassDescription 常量描述信息
     */
    void addConstant(Object constant, ConstantClassDescription constantClassDescription) {
        if (constant != null) {
            constants.put(constant.getClass(), constant);
            constantDescriptions.put(constant.getClass(), constantClassDescription);
        }
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
