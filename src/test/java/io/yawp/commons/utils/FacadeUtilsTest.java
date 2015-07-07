package io.yawp.commons.utils;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class FacadeUtilsTest {

	public interface SimpleFacade {
		String getName();
	}

	public static class Person implements SimpleFacade {
		private String name;
		private int age;

		public Person(String name, int age) {
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

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}
	}

	@Test
	public void testCopyingViaFacade() {
		Person from = new Person("Kala", 12);
		Person to = new Person("Seth", 17);
		FacadeUtils.copy(from, to, SimpleFacade.class);
		assertEquals("Kala", to.getName());
		assertEquals(17, to.getAge());
	}
}
