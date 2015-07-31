package cn.featherfly.constant.test;

import static org.junit.Assert.assertEquals;

import java.util.Map.Entry;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.yufei.Actor;
import org.yufei.Person;
import org.yufei.Role;
import org.yufei.Role2;
import org.yufei.User;

import cn.featherfly.constant.ConstantPool;

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
public class Test2 {
    
    ConstantPool pool;
    
    @BeforeClass
    public void setUp() {
        ConstantPool.init();
        pool = ConstantPool.getDefault();        
    }
    
    @Test
    public void test() {
        Role role = pool.getConstant(Role.class);
        ConstantTest constantTest = pool.getConstant(ConstantTest.class);
        User user = pool.getConstant(User.class);
        System.out.println(role.getName());
        System.out.println(role.getUser().getName());
        assertEquals(role.getName(), constantTest.getName());
        assertEquals(role.getUser().getName(), new User().getName());
        
        Role2 role2 = pool.getConstant(Role2.class);
        System.out.println(role2.getName());
        System.out.println(role2.getUser().getName());
        System.out.println("r.p.n : " + role2.getPerson().getName());
        System.out.println("r.p.a : " + role2.getPerson().getAge());
        assertEquals(role2.getName(), constantTest.getName());
        assertEquals(role2.getUser(), user);
        assertEquals(role2.getPerson().getName(), "p1");
        assertEquals(role2.getPerson().getAge(), new Integer(10));
        
        int index = 0;
        for (Person p : role2.getPersons()) {
            index++;
            System.out.println("index : " + index);
            System.out.println("r.pa.n : " + p.getName());
            System.out.println("r.pa.a : " + p.getAge());
            assertEquals(p.getName(), "p_array_" + index);
            assertEquals(p.getAge(), new Integer(20 + index));
        }
        index = 0;
        for (Person p : role2.getPersonList()) {
            index++;
            System.out.println("index : " + index);
            System.out.println("r.pa.n : " + p.getName());
            System.out.println("r.pa.a : " + p.getAge());
            assertEquals(p.getName(), "p_list_" + index);
            assertEquals(p.getAge(), new Integer(30 + index));
        }
    }
    
    public static void main(String[] args) {
        ConstantPool.init();
        ConstantPool pool = ConstantPool.getDefault();

        Role role = pool.getConstant(Role.class);
        System.out.println(role.getName());
        System.out.println(role.getUser().getName());
        Role2 role2 = pool.getConstant(Role2.class);
        System.out.println(role2.getName());
        System.out.println(role2.getUser().getName());
        System.out.println("r.p.n : " + role2.getPerson().getName());
        System.out.println("r.p.a : " + role2.getPerson().getAge());
        int index = 0;
        for (Person p : role2.getPersons()) {
            System.out.println("index : " + index);
            System.out.println("r.pa.n : " + p.getName());
            System.out.println("r.pa.a : " + p.getAge());
            index++;
        }
        index = 0;
        for (Person p : role2.getPersonList()) {
            System.out.println("index : " + index);
            System.out.println("r.pl.n : " + p.getName());
            System.out.println("r.pl.a : " + p.getAge());
            index++;
        }
        index = 0;
        for (Entry<String, Person> e : role2.getPersonMap().entrySet()) {
            System.out.println("index : " + index);
            System.out.println("r.key: " + e.getKey());
            System.out.println("r.pm.n : " + e.getValue().getName());
            System.out.println("r.pm.a : " + e.getValue().getAge());
            index++;
        }

        System.out.println("actor:");
        System.out.println("r.a.n : " + role2.getActor().getName());
        index = 0;
        for (Actor a : role2.getActors()) {
            System.out.println("index : " + index);
            System.out.println("r.as.n : " + a.getName());
            index++;
        }
        index = 0;
        for (Actor a : role2.getActorList()) {
            System.out.println("index : " + index);
            System.out.println("r.al.n : " + a.getName());
            index++;
        }

    }
}
