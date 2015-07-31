
package org.yufei;

/**
 * <p>
 * Person
 * </p>
 *
 * @author 钟冀
 */
public class Person implements Actor{

	private String name;

	private Integer age;

	/**
	 * 返回name
	 * @return name
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * 设置name
	 * @param name name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 返回age
	 * @return age
	 */
	public Integer getAge() {
		return age;
	}

	/**
	 * 设置age
	 * @param age age
	 */
	public void setAge(Integer age) {
		this.age = age;
	}
}
