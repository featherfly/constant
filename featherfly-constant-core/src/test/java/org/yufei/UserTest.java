
package org.yufei;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import cn.featherfly.constant.ConstantException;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.Modifier;
import javassist.NotFoundException;

/**
 * <p>
 * UserTest
 * </p>
 * 
 * @author zhongj
 */
public class UserTest {

    public static Class<?> t(String className) {
        Class<?> type = null;
        ClassPool pool = ClassPool.getDefault();
        try {
            CtClass ctClass = pool.get(className);
            if (ctClass.isInterface()
                    || Modifier.isAbstract(ctClass.getModifiers())) {
                // String dynamicClassName = ctClass.getPackageName() + "._"
                // + ctClass.getSimpleName() + "DynamicImpl";
                // CtClass dynamicCtClass =
                // pool.makeClass(dynamicClassName);
                // for (CtMethod method : ctClass.getMethods()) {
                // CtMethod ctMethod = new CtMethod(method.getReturnType(),
                // method.getName(), method.getParameterTypes(),
                // dynamicCtClass);
                // ctMethod.setBody("");
                // dynamicCtClass.addMethod(ctMethod);
                // }
                // ctClass = dynamicCtClass;
                // TODO 这里加入接口和抽象类的支持
            } else {
                boolean hasDefaultConstructor = false;
                for (CtConstructor ctc : ctClass.getDeclaredConstructors()) {
                    if (!javassist.Modifier.isPrivate(ctc.getModifiers())) {
                        throw new ConstantException(String.format(
                                "@ConstantClass标注的可实例化类%s只能拥有私有构造方法",
                                className));
                    }
                    if (ctc.getParameterTypes().length == 0) {
                        ctc.setModifiers(javassist.Modifier.PUBLIC);
                        // ctClass.removeConstructor(ctc);
                        hasDefaultConstructor = true;
                    }
                }
                if (!hasDefaultConstructor) {
                    throw new ConstantException(String.format(
                            "@ConstantClass标注的可实例化类%s必须有没有参数的私有构造方法",
                            className));
                }
            }

            // CtConstructor ctConstructor = new CtConstructor(new CtClass[0],
            // ctClass);
            // ctConstructor.setModifiers(javassist.Modifier.PUBLIC);
            // ctConstructor.setBody("super();");
            // ctClass.addConstructor(ctConstructor);

            type = ctClass.toClass();
            ctClass.detach();
        } catch (NotFoundException e) {
            throw new ConstantException(
                    String.format("常量配置类%s没有找到", className));
        } catch (CannotCompileException e) {
            throw new ConstantException(String.format("常量配置类%s预处理报错:%s",
                    className, e.getMessage()));
        }
        return type;
    }

    static Object newInstance(Class<?> constantType) throws Exception,
            IllegalArgumentException, InvocationTargetException {
        Constructor<?> constructor = constantType
                .getDeclaredConstructor(new Class<?>[0]);
        constructor.setAccessible(true);
        return constructor.newInstance(new Object[0]);
    }

    public static void main(String[] args) throws Exception {
        System.out.println(t("org.yufei.User").newInstance());
        // System.out.println(newInstance(ClassUtils.forName("org.yufei.User")));
    }
}
