package cn.featherfly.component.constant.listener;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.type.classreading.MetadataReader;

import cn.featherfly.component.constant.ConstantConfigurate;
import cn.featherfly.component.constant.ConstantPool;
import cn.featherfly.component.constant.configuration.ConstantParameter;
import cn.featherfly.component.scope.ApplicationScopeEvent;
import cn.featherfly.component.scope.ApplicationScopeListener;
import cn.featherfly.component.scope.ClassMetadataSupport;

/**
 * <p>
 * 可配置常量的监听器
 * </p>
 *
 * @author 钟冀
 */
public class ConstantApplicationScopeListener implements ApplicationScopeListener, ClassMetadataSupport {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ConstantApplicationScopeListener.class);

    /**
     */
    public ConstantApplicationScopeListener() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void contextStartup(ApplicationScopeEvent event) {
        if (ConstantPool.getConstant(ConstantParameter.class).isReParse()) {
            LOGGER.debug("常量配置重新解析[ConstantParameter.reParse]");
            ConstantConfigurate.getConstantConfigurate().parse(ConstantPool.getConstants());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void shutdown(ApplicationScopeEvent event) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startup(ApplicationScopeEvent event) {
        ConstantConfigurate.init(metaSet);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMetadataReaders(Set<MetadataReader> metaSet) {
        this.metaSet = metaSet;
    }

    // ********************************************************************
    //    property
    // ********************************************************************

    private Set<MetadataReader> metaSet;
}
