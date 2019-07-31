
package cn.featherfly.constant.spring;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import cn.featherfly.constant.ConstantConfigurator;
import cn.featherfly.constant.ConstantPool;

/**
 * <p>
 * 自动注册constant对象到spring context
 * </p>
 *
 * @author 钟冀
 */
public class ConstantConfigurer implements BeanFactoryPostProcessor {

    /**
     * logger
     */
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * {@inheritDoc}
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        logger.debug("start regist constant to spring");
        if (!ConstantPool.isInit()) {
            ConstantConfigurator.config();
        }
        ConstantPool pool = ConstantPool.getDefault();
        Collection<?> constants = pool.getConstants();
        for (Object constant : constants) {
            logger.debug("regist -> {} ", constant.getClass().getName());

            beanFactory.registerSingleton(constant.getClass().getName(), constant);
        }
        logger.debug("end regist constant to spring");
    }
}
