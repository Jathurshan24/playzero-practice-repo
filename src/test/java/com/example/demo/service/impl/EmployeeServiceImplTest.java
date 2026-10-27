package com.example.demo.service.impl;

import com.example.demo.entity.Employee;
import com.example.demo.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmployeeServiceImplTest {

	private final Map<Integer, Employee> store = new LinkedHashMap<>();
	private int nextId;
	private EmployeeServiceImpl service;

	@BeforeEach
	void setUp() {
		store.clear();
		nextId = 1;
		service = new EmployeeServiceImpl();
		ReflectionTestUtils.setField(service, "employeeRepository", inMemoryRepository());
	}

	@Test
	void createdEmployeeIdRoundTripsThroughRepositoryAndService() {
		Employee saved = service.createEmployee(new Employee());

		Integer id = saved.getId();
		assertNotNull(id);
		assertSame(saved, service.getEmployeeById(id));
	}

	@Test
	void updateAndDeleteAcceptTheGeneratedId() {
		Employee saved = service.createEmployee(new Employee());
		Employee changes = new Employee();
		changes.setFirstName("Jane");
		changes.setLastName("Roe");
		changes.setEmail("jroe@xyz.com");

		Employee updated = service.updateEmployee(saved.getId(), changes);
		assertEquals(saved.getId(), updated.getId());
		assertEquals("Jane", updated.getFirstName());

		service.deleteEmployeeById(saved.getId());
		assertFalse(store.containsKey(saved.getId()));
	}

	@Test
	void employeeWithMaximumIntegerIdCanBeLoaded() {
		Employee employee = new Employee();
		employee.setId(Integer.MAX_VALUE);
		store.put(Integer.MAX_VALUE, employee);

		assertSame(employee, service.getEmployeeById(Integer.MAX_VALUE));
	}

	@Test
	void unknownIdIsReportedAsNotFound() {
		RuntimeException ex = assertThrows(RuntimeException.class, () -> service.getEmployeeById(42));

		assertEquals("Employee not found with the id: 42", ex.getMessage());
	}

	private EmployeeRepository inMemoryRepository() {
		return (EmployeeRepository) Proxy.newProxyInstance(
				EmployeeRepository.class.getClassLoader(),
				new Class<?>[]{EmployeeRepository.class},
				(proxy, method, args) -> switch (method.getName()) {
					case "save" -> {
						Employee employee = (Employee) args[0];
						if (employee.getId() == null) {
							employee.setId(nextId++);
						}
						store.put(employee.getId(), employee);
						yield employee;
					}
					case "findById" -> Optional.ofNullable(store.get((Integer) args[0]));
					case "findAll" -> new ArrayList<>(store.values());
					case "delete" -> {
						store.remove(((Employee) args[0]).getId());
						yield null;
					}
					case "hashCode" -> System.identityHashCode(proxy);
					case "equals" -> proxy == args[0];
					case "toString" -> "InMemoryEmployeeRepository";
					default -> throw new UnsupportedOperationException(method.getName());
				});
	}
}
