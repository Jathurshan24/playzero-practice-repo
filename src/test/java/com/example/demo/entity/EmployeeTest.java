package com.example.demo.entity;

import com.example.demo.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EmployeeTest {

	@Test
	void newEmployeeHasNoIdUntilPersisted() {
		assertNull(new Employee().getId());
	}

	@Test
	void idAccessorsKeepIntegerBoundaryValues() {
		int[] values = {Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE};
		for (int value : values) {
			Employee employee = new Employee();
			employee.setId(value);
			assertEquals(Integer.valueOf(value), employee.getId());
		}
	}

	@Test
	void setIdAcceptsNullWithoutSubstitutingZero() {
		Employee employee = new Employee();
		employee.setId(1);
		employee.setId(null);
		assertNull(employee.getId());
	}

	@Test
	void idTypeMatchesRepositoryIdType() throws Exception {
		assertEquals(Integer.class, Employee.class.getDeclaredField("id").getType());
		assertEquals(Integer.class, Employee.class.getMethod("getId").getReturnType());
		assertEquals(void.class, Employee.class.getMethod("setId", Integer.class).getReturnType());
		assertEquals(Integer.class, repositoryIdType());
	}

	private static Type repositoryIdType() {
		for (Type type : EmployeeRepository.class.getGenericInterfaces()) {
			if (type instanceof ParameterizedType parameterized) {
				return parameterized.getActualTypeArguments()[1];
			}
		}
		throw new IllegalStateException("EmployeeRepository has no parameterized super-interface");
	}
}
