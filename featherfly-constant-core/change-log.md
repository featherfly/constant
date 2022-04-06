TODO 
    对@ConstantClass标注对象加入对接口和抽象类的支持

# 1.6.6 2022-04-06
1. 修复common-core不兼容升级

2021-04-28
1.6.5
    1.修复springboot启动时javassit找不到类的问题
    
2020-12-11
1.6.4
    1.ConstantConfigurator.config加入传入ClassLoader参数方法，应对fatjar打包模式

2020-4-11
1.6.3
    1.合并constant-spring到constant-core
    2.升级依赖
    
2019-12-03
1.6.2
    1.升级依赖
    
2019-11-21
1.6.1
    1.修复会错误的自动加载默认配置文件的问题

2019-11-21
1.6.0
    1.升级Converssion版本
    2.处理配置文件入口的文件未找到时抛出异常，而不是出现空指针异常
    3.变更group名称为cn.featherfly.constant

2019-08-26
1.5.1
    1.ConstantParameter的Parser加入ClassFieldParser, ClassMethodParser, YamlBeanPropertyParser
    
2019-08-25
1.5.0
    1.默认配置文件从ApplicationConstant.xml变为constant.yaml
    2.constant.yaml加入简化支持
        basePackeges: cn.featherfly,org.yufei
        configFiles: 
          - importConstant.yaml
          - Constant-Role3.yaml
        reParse: true
        parsers: > 
            cn.featherfly.conversion.parse.ClassParser
            ,cn.featherfly.conversion.parse.JsonBeanPropertyParser
            ,cn.featherfly.constant.parse.ConstantParser
    3.实现@ConstantClass标注类不能有private构造方法以外的构造方法并且必须有一个无参数私有构造方法
    4.ConstantParameter加入cfgFileLoader配置属性用于配置文件加载器，默认实现了ClassLoader加载器，spring集成包实现了ResourcePatternResolver加载器
                  

2019-08-20
1.4.4
    1.升级conversion依赖
    
2019-07-31
1.4.3
    1.ConstantPool加入getConstantParameter()方法
    2.ConstantConfigurator加入config()方法 
    
2019-07-31
1.4.2
    1.升级featherfly-common到1.7.2
    

2017-04-19
1.4.1
	1.升级featherfly-common到1.6.0,删除内部的WhiteBlackPolity
	2.删除老的不再使用的类
	3.使用java8编译
	
2017-04-14
1.4.0
	1.实现配置写在多个文件中
	2.实现yaml格式配置文件（参考测试项目）
	3.xml文件格式支持类名和属性名直接作为节点名（参考测试项目）
	4.实现spring自动注册
	
2015-04-24
1.3.0
	1.重构
	
2014-06-21
1.2.0
	1.移植cn.featherfly
2013-11-11
1.1.5
	1.重新打包，使ConstantPool可以在JSP中使用

2013-09-04
1.1.4
	1.加入devMode参数

2013-07-13
1.1.3
	1.修正JsonParser空指针异常
	2.修正AbstractIterableParser，在目标对象为数组或集合时，只有单个值报错的问题

2013-07-06
1.1.2
	1.加入AbstractIterableParser抽象类，实现数组的配置的基本支持
	2.ClassParser,ConstantParsr实现BeanPropertyParser接口，实现数组和集合的转换
		只需要把多个值用逗号隔开即可


2013-03-27
1.1.1
	1.取值时，如果为空，则不调用类型转换

2012-12-28 1.1.0
	1.加入parse包，支持带有协议的数据设置，本组件自带class:和constant:和json:支持
	<constants class="com.thgk.Role">
		1.class支持
			代表new一个对象赋值
			<constant name="user" value="class:com.thgk.User"/>

		2.constant支持
			1.代表从constant中查找指定类赋值
			<constant name="user" value="constant:com.thgk.User"/>
			2.代表从constant中查找指定类并使用指定属性赋值
			<constant name="name" value="constant:cn.featherfly.test.ConstantTest#name"/>

		3.JSON支持
			1.代表从使用JSON组装类型为com.thgk.User对象,注意#后面不能有空格，并且要跟JSON的解析符号，即#{和#[
			<constant name="user">
				json:com.thgk.User#{
					name : yufei,
					age : 20
				}
			</constant>
			2.代表从使用JSON组装user对应类型的对象
			<constant name="user">
				json:{
					name : yufei,
					age : 20
				}
			</constant>
	</constants>
	2.支持<constant name="user">class:com.thgk.User</constant>写法

2011-3-9 1.0.2
	1.加入init(Set<MetadataReader> metaSet)，如果设置了指定的metaSet将不再自己扫描
	  如果metaSet为null将使用basePackage继续扫描
	2.类属性常量加入数组支持
	3.字符串属性basePackage改为字符串数组basePackages，加入多包搜索支持，多个包使用逗号隔开

2011-11-1 1.0.1
	1.取消ConstantPool.addConstant的public访问级别
	2.移动ConstantConfigurate的包位置，

2010-12-27 1.0.0
发布，支持可配置常量和信息查找