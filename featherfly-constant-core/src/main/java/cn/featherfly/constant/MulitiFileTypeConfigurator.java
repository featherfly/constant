package cn.featherfly.constant;

import java.net.URL;
import java.util.Collection;

import cn.featherfly.constant.description.ConstantClassDescription;

/**
 * <p>
 * 多文件类型配置读取可配置常量配置读取.
 * </p>
 *
 * @author 钟冀
 */
public class MulitiFileTypeConfigurator extends AbstractConfigurator {

    private AbstractConfigurator configurator;

    // ********************************************************************
    // 构造方法
    // ********************************************************************

    /**
     * @param defaultConfigurator defaultConfigurator
     */
    MulitiFileTypeConfigurator(DefaultConfigurator defaultConfigurator) {
        super(null, null, null, null);
        configurator = defaultConfigurator;
        logger.debug("configurator -> {}", configurator.getClass().getName());
    }

    /**
     * @param file
     * @param abstractConfigurators
     */
    MulitiFileTypeConfigurator(URL file, AbstractConfigurator... abstractConfigurators) {
        super(file, null, null, null);
        for (AbstractConfigurator abstractConfigurator : abstractConfigurators) {
            if (abstractConfigurator
                    .match(org.apache.commons.lang3.StringUtils.substringAfterLast(file.getPath(), "."))) {
                configurator = abstractConfigurator;
                logger.debug("configurator -> {}", configurator.getClass().getName());
                return;
            }
        }
        throw new ConstantException("不支持的文件类型[" + file.getPath() + "]，扩展名不正确");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getConstant(Class<T> type) {
        return configurator.getConstant(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasConstant(Class<?> type) {
        return configurator.hasConstant(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<?> getConstants() {
        return configurator.getConstants();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConstantClassDescription getConstantDescription(Class<?> type) {
        return configurator.getConstantDescription(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<ConstantClassDescription> getConstantDescriptions() {
        return configurator.getConstantDescriptions();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Collection<Object> readCfg(URL cfgFile) {
        return configurator.readCfg(cfgFile);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean match(String fileExtName) {
        return configurator.match(fileExtName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void load() {
        configurator.load();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addConstant(Object constant, boolean onMerge) {
        configurator.addConstant(constant, onMerge);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void parse(Collection<?> constantList) {
        configurator.parse(constantList);
    }
}
