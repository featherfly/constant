
package cn.featherfly.constant;

import cn.featherfly.common.exception.LocalizedException;

/**
 * <p>
 * 配置读取解析的时候发生的异常.
 * </p>
 * 
 * @author 钟冀
 */
public class ConstantException extends LocalizedException {

    private static final long serialVersionUID = 1348668900325588507L;

    private static final String MSG_PRE = "解析配置文件出错 ";

    /**
     * 构造方法
     */
    public ConstantException() {
        super(MSG_PRE);
    }

    /**
     * 构造方法
     * 
     * @param msg
     *            信息
     */
    public ConstantException(String msg) {
        super(msg);
    }

    /**
     * 构造方法
     * 
     * @param t
     *            异常
     */
    public ConstantException(Throwable t) {
        super(t);
    }

    /**
     * 构造方法
     * 
     * @param msg
     *            信息
     * @param t
     *            异常
     */
    public ConstantException(String msg, Throwable t) {
        super(msg, t);
    }
}
