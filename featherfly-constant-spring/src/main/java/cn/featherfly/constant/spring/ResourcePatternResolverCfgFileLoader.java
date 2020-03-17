
package cn.featherfly.constant.spring;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import cn.featherfly.constant.CfgFileLoader;
import cn.featherfly.constant.ConstantException;

/**
 * <p>
 * CfgFileReaderClassLoaderImpl
 * </p>
 * 
 * @author zhongj
 */
public class ResourcePatternResolverCfgFileLoader implements CfgFileLoader {

    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    /**
     * {@inheritDoc}
     */
    @Override
    public List<URL> load(String filePath) {
        List<URL> list = new ArrayList<>();
        try {
            Resource[] resources = resourcePatternResolver
                    .getResources(filePath);
            for (Resource resource : resources) {
                list.add(resource.getURL());
            }
            return list;
        } catch (IOException e) {
            throw new ConstantException("使用路径" + filePath + "扫描文件时发生I/O异常", e);
        }
    }

}
