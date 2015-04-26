
package cn.featherfly.constant.description;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 常量类的描述信息
 * </p>
 *
 * @author 钟冀
 */
public class ConstantClassDescription {

    /**
     * 构造函数
     * @param name 名称
     * @param descp 描述
     * @param constantClass 常量类类型
     */
    public ConstantClassDescription(String name, String descp, Class<?> constantClass) {
        this.name = name;
        this.descp = descp;
        this.constantClass = constantClass;
    }

    /**
     * <p>
     * 添加constantDescription
     * </p>
     * @param constantDescription constantDescription
     */
    public void addConstantDescription(ConstantDescription constantDescription) {
        constantDescriptions.add(constantDescription);
    }

    private String name;

    private String descp;

    private Class<?> constantClass;

    private List<ConstantDescription> constantDescriptions = new ArrayList<ConstantDescription>();

    /**
     * 返回name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * 返回descp
     * @return descp
     */
    public String getDescp() {
        return descp;
    }

    /**
     * 返回constantClass
     * @return constantClass
     */
    public Class<?> getConstantClass() {
        return constantClass;
    }

    /**
     * 返回constantDescriptions
     * @return constantDescriptions
     */
    public List<ConstantDescription> getConstantDescriptions() {
        return new ArrayList<ConstantDescription>(constantDescriptions);
    }



}
