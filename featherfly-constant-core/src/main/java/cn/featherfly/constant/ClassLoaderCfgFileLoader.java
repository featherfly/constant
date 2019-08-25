
package cn.featherfly.constant;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.featherfly.common.lang.ClassLoaderUtils;

/**
 * <p>
 * CfgFileReaderClassLoaderImpl
 * </p>
 * 
 * @author zhongj
 */
public class ClassLoaderCfgFileLoader implements CfgFileLoader {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<URL> load(String filePath) {
        List<URL> list = new ArrayList<>();
        list.add(ClassLoaderUtils.getResource(filePath,
                ClassLoaderCfgFileLoader.class));
        return list;
    }

}
