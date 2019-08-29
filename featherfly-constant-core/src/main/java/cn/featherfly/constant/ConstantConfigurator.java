package cn.featherfly.constant;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.core.type.classreading.MetadataReader;

import cn.featherfly.common.bean.BeanUtils;
import cn.featherfly.common.io.ClassPathScanningProvider;
import cn.featherfly.common.lang.ClassUtils;
import cn.featherfly.common.lang.LangUtils;
import cn.featherfly.common.lang.StringUtils;
import cn.featherfly.constant.annotation.ConstantClass;
import cn.featherfly.constant.configuration.ConstantParameter;
import cn.featherfly.conversion.core.ConversionPolicy;
import cn.featherfly.conversion.core.ConversionPolicys;
import cn.featherfly.conversion.parse.ParsePolity;
import cn.featherfly.conversion.parse.Parser;

/**
 * <p>
 * 可配置常量配置读取.
 * </p>
 *
 * @author 钟冀
 */
public class ConstantConfigurator extends MulitiFileTypeConfigurator {

    public static final String DEFAULT_FILE = "constant.yaml";

    // ********************************************************************
    // 构造方法
    // ********************************************************************

    /**
     * @param abstractConfigurators abstractConfigurators
     */
    private ConstantConfigurator(String fileName, AbstractConfigurator... abstractConfigurators) {
        super(fileName, abstractConfigurators);
    }
    //
    // /**
    // * @param conversionPolicy
    // * conversionPolicy
    // * @param parsePolity
    // * parsePolity
    // */
    // ConstantConfigurator(String fileName, ConversionPolicy conversionPolicy,
    // ParsePolity parsePolity, ConstantPool constantPool) {
    // this(fileName, new DOMConfigurator(fileName, conversionPolicy,
    // parsePolity, constantPool)
    // , new YAMLConfigurator(fileName, conversionPolicy, parsePolity,
    // constantPool));
    // }

    public static ConstantConfigurator config() {
        return config(DEFAULT_FILE);
    }

    public static ConstantConfigurator config(String fileName) {
        return config(fileName, ConversionPolicys.getFormatConversionPolicy(), null);
    }

    public static ConstantConfigurator config(ConversionPolicy conversionPolicy) {
        return config(conversionPolicy, null);
    }

    public static ConstantConfigurator config(ConversionPolicy conversionPolicy, ParsePolity parsePolity) {
        return config(DEFAULT_FILE, conversionPolicy, parsePolity);
    }

    public static ConstantConfigurator config(String fileName, ConversionPolicy conversionPolicy,
            ParsePolity parsePolity) {
        ConstantParameter config = ConstantParameter.DEFAULT;
        if (parsePolity == null) {
            parsePolity = initParserPolity(config);
        }
        ConstantPool constantPool = ConstantPool.init();
        DOMConfigurator domConfigurator = new DOMConfigurator(fileName, conversionPolicy, parsePolity, constantPool);
        domConfigurator.getFilterTypePolicy().clear().setEnableWhiteList(false).addBlack(ConstantParameter.class);

        YAMLConfigurator yamlConfigurator = new YAMLConfigurator(fileName, conversionPolicy, parsePolity, constantPool);
        yamlConfigurator.getFilterTypePolicy().clear().setEnableWhiteList(false).addBlack(ConstantParameter.class);
        ConstantConfigurator configurator = new ConstantConfigurator(fileName, domConfigurator, yamlConfigurator);
        configurator.addConstant(config, false);
        configurator.load();
        // configurator.parse(configurator.getConstants());
        config = configurator.getConstant(config.getClass());

        // Collection<Object> constants =
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
        // for (String file : configFiles) {
        // MulitiFileTypeConfigurator mulitiFileTypeConfigurator = new
        // MulitiFileTypeConfigurator(
        // file,
        // new DOMConfigurator(file, conversionPolicy, parsePolity,
        // constantPool),
        // new YAMLConfigurator(file, conversionPolicy, parsePolity,
        // constantPool));
        // mulitiFileTypeConfigurator.load();
        // configurators.add(mulitiFileTypeConfigurator);
        // }
        for (AbstractConfigurator c : configurators) {
            // System.err.println("parse " + c.getClass().getName());
            c.parse(configurator.getConstants());
        }
        return configurator;
    }

    private static ParsePolity initParserPolity(ConstantParameter config) {
        ParsePolity parsePolity = new ParsePolity();
        for (Class<?> parserClass : config.getParsers()) {
            if (!ClassUtils.isParent(Parser.class, parserClass)) {
                throw new ConstantException(StringUtils.format("为[#1]的配置项parsers配置的参数[#2]不是[#3]的实现类",
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
        if (LangUtils.isEmpty(basePackages)) {
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
