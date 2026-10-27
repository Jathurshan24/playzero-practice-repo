package com.example.demo.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EmployeeTest {

	@Test
	void newEmployeeHasNullIdUntilPersisted() {
		Employee employee = new Employee();

		assertNull(employee.getId());
	}

	@Test
	void idCanBeClearedBackToNull() {
		Employee employee = new Employee();
		employee.setId(5);

		employee.setId(null);

		assertNull(employee.getId());
	}

	@Test
	void idAccessorsKeepIntegerBoundaryValues() {
		Employee employee = new Employee();

		employee.setId(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, employee.getId());

		employee.setId(Integer.MIN_VALUE);
		assertEquals(Integer.MIN_VALUE, employee.getId());

		employee.setId(0);
		assertEquals(0, employee.getId());
	}

	@Test
	void idTypeMatchesRepositoryIdType() throws NoSuchFieldException, NoSuchMethodException {
		assertEquals(Integer.class, Employee.class.getDeclaredField("id").getType());
		assertEquals(Integer.class, Employee.class.getMethod("getId").getReturnType());
		assertEquals(Integer.class, Employee.class.getMethod("setId", Integer.class).getParameterTypes()[0]);
	}
}
