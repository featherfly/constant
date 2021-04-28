package cn.featherfly.constant;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.core.type.classreading.MetadataReader;

import cn.featherfly.common.io.ClassPathScanningProvider;
import cn.featherfly.common.lang.ClassLoaderUtils;
import cn.featherfly.common.lang.ClassUtils;
import cn.featherfly.common.lang.Lang;
import cn.featherfly.common.lang.Strings;
import cn.featherfly.constant.annotation.ConstantClass;
import cn.featherfly.constant.configuration.ConstantParameter;
import cn.featherfly.conversion.parse.ParsePolity;
import cn.featherfly.conversion.parse.Parser;
import cn.featherfly.conversion.string.ToStringConversionPolicy;
import cn.featherfly.conversion.string.ToStringConversionPolicys;

/**
 * <p>
 * 可配置常量配置读取.
 * </p>
 *
 * @author 钟冀
 */
public class ConstantConfigurator extends MulitiFileTypeConfigurator {

    /** The Constant DEFAULT_FILE. */
    public static final String DEFAULT_FILE = "constant.yaml";

    // ********************************************************************
    // 构造方法
    // ********************************************************************

    /**
     * @param abstractConfigurators abstractConfigurators
     */
    private ConstantConfigurator(URL file, AbstractConfigurator... abstractConfigurators) {
        super(file, abstractConfigurators);
    }

    /**
     * Instantiates a new constant configurator.
     *
     * @param defaultConfigurator defaultConfigurator
     */
    public ConstantConfigurator(DefaultConfigurator defaultConfigurator) {
        super(defaultConfigurator);
    }

    /**
     * config.
     *
     * @return ConstantConfigurator
     */
    public static ConstantConfigurator config() {
        return config(ToStringConversionPolicys.getFormatConversionPolicy());
    }

    /**
     * config.
     *
     * @param classLoader the class loader
     * @return ConstantConfigurator
     */
    public static ConstantConfigurator config(ClassLoader classLoader) {
        return config(ToStringConversionPolicys.getFormatConversionPolicy(), classLoader);
    }

    /**
     * config.
     *
     * @param conversionPolicy conversionPolicy
     * @return ConstantConfigurator
     */
    public static ConstantConfigurator config(ToStringConversionPolicy conversionPolicy) {
        return config(conversionPolicy, null, null);
    }

    /**
     * config.
     *
     * @param conversionPolicy conversionPolicy
     * @param classLoader      the class loader
     * @return ConstantConfigurator
     */
    public static ConstantConfigurator config(ToStringConversionPolicy conversionPolicy, ClassLoader classLoader) {
        return config(conversionPolicy, null, classLoader);
    }

    /**
     * config.
     *
     * @param conversionPolicy conversionPolicy
     * @param parsePolity      parsePolity
     * @return ConstantConfigurator
     */
    public static ConstantConfigurator config(ToStringConversionPolicy conversionPolicy, ParsePolity parsePolity) {
        return config(DEFAULT_FILE, conversionPolicy, parsePolity, null, false);
    }

    /**
     * config.
     *
     * @param conversionPolicy conversionPolicy
     * @param parsePolity      parsePolity
     * @param classLoader      the class loader
     * @return ConstantConfigurator
     */
    public static ConstantConfigurator config(ToStringConversionPolicy conversionPolicy, ParsePolity parsePolity,
            ClassLoader classLoader) {
        return config(DEFAULT_FILE, conversionPolicy, parsePolity, classLoader, false);
    }

    /**
     * config.
     *
     * @param fileName fileName
     * @return ConstantConfigurator
     */
    public static ConstantConfigurator config(String fileName) {
        return config(fileName, ToStringConversionPolicys.getFormatConversionPolicy(), null);
    }

    /**
     * config.
     *
     * @param fileName    fileName
     * @param classLoader the class loader
     * @return ConstantConfigurator
     */
    public static ConstantConfigurator config(String fileName, ClassLoader classLoader) {
        return config(fileName, ToStringConversionPolicys.getFormatConversionPolicy(), null, classLoader);
    }

    /**
     * config.
     *
     * @param fileName         fileName
     * @param conversionPolicy conversionPolicy
     * @param parsePolity      parsePolity
     * @return ConstantConfigurator
     */
    public static ConstantConfigurator config(String fileName, ToStringConversionPolicy conversionPolicy,
            ParsePolity parsePolity) {
        return config(fileName, conversionPolicy, parsePolity, null);
    }

    /**
     * config.
     *
     * @param fileName         fileName
     * @param conversionPolicy conversionPolicy
     * @param parsePolity      parsePolity
     * @param classLoader      the class loader
     * @return ConstantConfigurator
     */
    public static ConstantConfigurator config(String fileName, ToStringConversionPolicy conversionPolicy,
            ParsePolity parsePolity, ClassLoader classLoader) {
        return config(fileName, conversionPolicy, parsePolity, classLoader, true);
    }

    private static ConstantConfigurator config(String fileName, ToStringConversionPolicy conversionPolicy,
            ParsePolity parsePolity, ClassLoader classLoader, boolean throwExceptionWhenFileNotFound) {
        ConstantParameter config = ConstantParameter.DEFAULT;
        if (parsePolity == null) {
            parsePolity = initParserPolity(config);
        }
        ConstantPool constantPool = ConstantPool.init();
        URL configFile = loadFile(fileName, throwExceptionWhenFileNotFound);
        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        ConstantConfigurator configurator = null;
        if (configFile == null) {
            configurator = new ConstantConfigurator(
                    new DefaultConfigurator(conversionPolicy, parsePolity, constantPool));
            configurator.addConstant(config, false);

            config = configurator.getConstant(config.getClass());
            configurator.classLoader = classLoader;
            configurator.scanConstants();
        } else {
            DOMConfigurator domConfigurator = new DOMConfigurator(configFile, conversionPolicy, parsePolity,
                    constantPool);
            domConfigurator.getFilterTypePolicy().clear().setEnableWhiteList(false).addBlack(ConstantParameter.class);

            YAMLConfigurator yamlConfigurator = new YAMLConfigurator(configFile, conversionPolicy, parsePolity,
                    constantPool);
            yamlConfigurator.getFilterTypePolicy().clear().setEnableWhiteList(false).addBlack(ConstantParameter.class);

            configurator = new ConstantConfigurator(configFile, domConfigurator, yamlConfigurator);
            configurator.addConstant(config, false);
            configurator.load();

            config = configurator.getConstant(config.getClass());
            configurator.classLoader = classLoader;
            configurator.scanConstants();
            List<AbstractConfigurator> configurators = new ArrayList<>();

            Set<String> configFiles = new HashSet<>();
            configFiles.add(fileName);
            for (String file : config.getConfigFiles()) {
                configFiles.add(file);
            }
            CfgFileLoader cfgFileLoader = config.getCfgFileLoader();

            Set<URL> cfgFilesURL = new HashSet<>();
            for (String file : configFiles) {
                cfgFilesURL.addAll(cfgFileLoader.load(file));
            }
            for (URL file : cfgFilesURL) {
                MulitiFileTypeConfigurator mulitiFileTypeConfigurator = new MulitiFileTypeConfigurator(file,
                        new DOMConfigurator(file, conversionPolicy, parsePolity, constantPool),
                        new YAMLConfigurator(file, conversionPolicy, parsePolity, constantPool));
                mulitiFileTypeConfigurator.load();
                configurators.add(mulitiFileTypeConfigurator);
            }
            for (AbstractConfigurator c : configurators) {
                c.parse(configurator.getConstants());
            }
        }
        return configurator;
    }

    private static URL loadFile(String fileName, boolean throwExceptionWhenFileNotFound) {
        URL url = ClassLoaderUtils.getResource(fileName, AbstractConfigurator.class);
        if (url == null && throwExceptionWhenFileNotFound) {
            throw new ConstantException("未找到文件：" + fileName);
        }
        return url;
    }

    private static ParsePolity initParserPolity(ConstantParameter config) {
        ParsePolity parsePolity = new ParsePolity();
        for (Class<?> parserClass : config.getParsers()) {
            if (!ClassUtils.isParent(Parser.class, parserClass)) {
                throw new ConstantException(Strings.format("为[{0}]的配置项parsers配置的参数[{1}]不是[{2}]的实现类",
                        config.getClass().getName(), parserClass.getName(), Parser.class.getName()));
            } else {
                parsePolity.register((Parser) BeanUtils.instantiateClass(parserClass));
            }
        }
        return parsePolity;
    }

    // ********************************************************************
    // 方法
    // ********************************************************************

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean filter(Class<?> type) {
        return type != ConstantParameter.class;
    }

    /**
     * <p>
     * 找到配置的basePackages下扫描到的所有常量配置对象（默认值）并添加到常量池中
     * </p>
     *
     * @return 常量配置对象
     */
    private Collection<Object> scanConstants() {
        ConstantParameter constantParameter = getConstant(ConstantParameter.class);
        String[] basePackages = constantParameter.getBasePackeges();
        if (Lang.isEmpty(basePackages)) {
            throw new ConstantException("常量对象扫描的起始包[basePackage]未指定");
        }
        ClassPathScanningProvider provider = new ClassPathScanningProvider();
        Set<MetadataReader> metaSet = new HashSet<>();
        for (String bp : basePackages) {
            metaSet.addAll(provider.findMetadata(bp));
        }
        List<Object> constants = new ArrayList<>();
        List<Class<?>> typeList = new ArrayList<>();
        for (MetadataReader metadataReader : metaSet) {
            String className = metadataReader.getClassMetadata().getClassName();
            try {
                if (metadataReader.getAnnotationMetadata().hasAnnotation(ConstantClass.class.getName())) {
                    typeList.add(replaceConstructors(className));
                }
            } catch (Exception e) {
                throw new ConstantException(String.format("常量配置类%s生成对象时发生异常：%s", className, e.getMessage()));
            }
        }
        typeList.forEach(type -> {
            if (hasConstant(type)) {
                logger.warn(String.format("重复的配置类[%s],使用忽略原则", type.getName()));
            } else {
                Object constant = newInstance(type);
                addConstant(constant, false);
                constants.add(constant);
            }
        });
        return constants;
    }

}
