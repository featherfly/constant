
package cn.featherfly.constant.parse;

import cn.featherfly.common.bean.BeanUtils;
import cn.featherfly.common.lang.GenericType;
import cn.featherfly.common.lang.LangUtils;
import cn.featherfly.constant.ConstantException;
import cn.featherfly.constant.ConstantPool;
import cn.featherfly.conversion.parse.AbstractIterableParser;
import cn.featherfly.conversion.parse.ParseException;


/**
 * <p>
 * constant常量协议解析器
 * </p>
 *
 * @author 钟冀
 */
public class ConstantParser<G extends GenericType<?>> extends AbstractIterableParser<G>{
    /**
     * 协议字符串常量
     */
    public static final String CLASS_PROTOCOL = "constant";

    /**
     */
    public ConstantParser() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProtocol() {
        return CLASS_PROTOCOL;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected <T> T doParseContent(String content, G to) {
        if (LangUtils.isEmpty(content) || !ConstantPool.isInit()) {
            return null;
        }
        try {
            String className = null;
            String propertyName = null;
            String[] cs = content.split("#");
            if (cs.length > 1) {
                propertyName = cs[1].trim();
            }
            className = cs[0].trim();
            Object constant = ConstantPool.getDefault().getConstant(Class.forName(className));
            if (constant == null) {
                throw new ConstantException("常量池不存在指定常量" + className);
            }
            if (LangUtils.isEmpty(propertyName)) {
                return (T) constant;
            } else {
                return (T) BeanUtils.getProperty(constant, propertyName);
            }
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean supportFor(GenericType<?> to) {
        return true;
    }
    /**
     * {@inheritDoc}
     */
   /* @Override
    protected Object doParseContent(String arg0, GenericType arg1) {
        if (LangUtils.isEmpty(content)) {
            return null;
        }
        try {
            String className = null;
            String propertyName = null;
            String[] cs = content.split("#");
            if (cs.length > 1) {
                propertyName = cs[1].trim();
            }
            className = cs[0].trim();
            Object constant = ConstantPool.getConstant(Class.forName(className));
            if (constant == null) {
                throw new ConstantException("常量池不存在指定常量" + className);
            }
            if (LangUtils.isEmpty(propertyName)) {
                return (T) constant;
            } else {
                return (T) BeanUtils.getProperty(constant, propertyName);
            }
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }
*/
   
}
