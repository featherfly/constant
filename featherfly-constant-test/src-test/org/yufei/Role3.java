
package org.yufei;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import cn.featherfly.constant.annotation.Constant;
import cn.featherfly.constant.annotation.ConstantClass;

/**
 * <p>
 * 类的说明放这里
 * </p>
 * <p>
 * copyright featherfly 2010-2020, all rights reserved.
 * </p>
 *
 * @author 钟冀
 */
@ConstantClass("角色")
public class Role3 {
	@Constant("名称")
	private String name = "admin";
	@Constant("用户")
	private User user;
	@Constant("人员")
	private Person person;
	@Constant("人员s")
	private Person[] persons;
	@Constant("人员list")
	private List<Person> personList;
	@Constant("人员map") 
	private Map<String, Person> personMap;
	@Constant("参与者")
	private Actor actor;
	@Constant("参与者s")
	private Actor[] actors;
	@Constant("参与者List")
	private List<Actor> actorList;

	/**
	 * 返回actorList
	 * @return actorList
	 */
	public List<Actor> getActorList() {
		return actorList;
	}

	/**
	 * 返回actors
	 * @return actors
	 */
	public Actor[] getActors() {
		return actors;
	}

	/**
	 * 返回actor
	 * @return actor
	 */
	public Actor getActor() {
		return actor;
	}

	/**
	 * 返回user
	 * @return user
	 */
	public User getUser() {
		return user;
	}

	public String getName() {
		return name;
	}

	/**
	 * 返回person
	 * @return person
	 */
	public Person getPerson() {
		return person;
	}

	/**
	 * 返回persons
	 * @return persons
	 */
	public Person[] getPersons() {
		return persons;
	}

	/**
	 * 返回personList
	 * @return personList
	 */
	public List<Person> getPersonList() {
		return personList;
	}

	/**
	 * 返回personMap
	 * @return personMap
	 */
	public Map<String, Person> getPersonMap() {
		return personMap;
	}

	public static void main(String[] args) throws SecurityException, NoSuchFieldException {
		Field f = Role3.class.getDeclaredField("personList");
		System.out.println(f.getGenericType());

		Type type = f.getGenericType();

		System.out.println(((ParameterizedType) type).getActualTypeArguments()[0]);

		System.out.println(type instanceof ParameterizedType);

		System.out.println();

		f = Role3.class.getDeclaredField("personLists");
		System.out.println(f.getGenericType());
		type = f.getGenericType();

		System.out.println(type instanceof ParameterizedType);

		System.out.println(((ParameterizedType) type).getActualTypeArguments()[0]);
	}

}
