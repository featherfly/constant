
package cn.featherfly.constant.description;


/**
 * <p>
 * 常量的描述信息
 * </p>
 *
 * @author 钟冀
 */
public class ConstantDescription {

    private String name;

    private String descp;

    private String value;

    private ConstantClassDescription constantClassDescription;

    /**
     * 构造函数
     * @param name 名称
     * @param descp 描述
     * @param value 值
     * @param constantClassDescription 常量类描述信息
     */
    public ConstantDescription(String name, String descp
            , String value, ConstantClassDescription constantClassDescription) {
        this.name = name;
        this.descp = descp;
        this.value = value;
        this.constantClassDescription = constantClassDescription;
    }

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
     * 返回value
     * @return value
     */
    public String getValue() {
        return value;
    }

    /**
     * 返回constantClassDescription
     * @return constantClassDescription
     */
    public ConstantClassDescription getConstantClassDescription() {
        return constantClassDescription;
    }


}
