
package cn.featherfly.constant;

import cn.featherfly.constant.description.ConstantClassDescription;

/**
 * <p>
 * ConstantContent
 * </p>
 * 
 * @author 钟冀
 */
public class ConstantContent {

    /**
     * 
     */
    ConstantContent() {
    }
    
    /**
     * @param constant constant
     * @param description description
     */
    ConstantContent(Object constant, ConstantClassDescription description) {
        this.description = description;
        this.constant = constant;
    }

    private ConstantClassDescription description;
    
    private Object constant;

    /**
     * 返回description
     * @return description
     */
    public ConstantClassDescription getDescription() {
        return description;
    }

    /**
     * 设置description
     * @param description description
     */
    void setDescription(ConstantClassDescription description) {
        this.description = description;
    }

    /**
     * 返回constant
     * @return constant
     */
    public Object getConstant() {
        return constant;
    }

    /**
     * 设置constant
     * @param constant constant
     */
    void setConstant(Object constant) {
        this.constant = constant;
    }    
}
