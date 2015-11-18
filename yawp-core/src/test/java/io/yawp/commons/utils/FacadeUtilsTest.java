package io.yawp.commons.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class FacadeUtilsTest {

    public interface SetFacade {
        void setAge(Integer age);
    }

    public interface GetFacade {
        String getName();
    }

    public static class Person implements SetFacade, GetFacade {
        private String name;

        private Integer age;

        public Person(String name, Integer age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }
    }

    @Test
    public void testSetViaFacade() {
        Person defaults = new Person("jim", 27);
        Person object = new Person("kurt", 22);

        FacadeUtils.set(object, defaults, SetFacade.class);

        assertEquals("jim", object.getName());
        assertEquals((Integer) 22, object.getAge());
    }

    @Test
    public void testGetViaFacade() {
        Person object = new Person("jim", 27);

        FacadeUtils.get(object, GetFacade.class);

        assertEquals("jim", object.getName());
        assertNull(object.getAge());
    }
}
