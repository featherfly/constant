
package cn.featherfly.constant;

import java.net.URL;
import java.util.List;

/**
 * <p>
 * FileLoader
 * </p>
 * 
 * @author zhongj
 */
public interface CfgFileLoader {

    List<URL> load(String filePathPattern);
}
