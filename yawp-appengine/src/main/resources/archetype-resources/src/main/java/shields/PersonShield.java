package ${package}.shields;

import io.yawp.repository.shields.Shield;
import ${package}.models.Person;

public class PersonShield extends Shield<Person> {

	@Override
	public void defaults() {
		allow();
	}

}
