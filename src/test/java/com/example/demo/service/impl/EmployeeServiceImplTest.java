package com.example.demo.service.impl;

import com.example.demo.entity.Employee;
import com.example.demo.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmployeeServiceImplTest {

	private final Map<Integer, Employee> store = new LinkedHashMap<>();
	private final List<Integer> idsReceivedBySave = new ArrayList<>();
	private int nextId = 1;
	private EmployeeServiceImpl service;

	@BeforeEach
	void setUp() {
		service = new EmployeeServiceImpl();
		ReflectionTestUtils.setField(service, "employeeRepository", inMemoryRepository());
	}

	@Test
	void createdEmployeeIdCanBeUsedToFetchTheEmployee() {
		Employee created = service.createEmployee(new Employee());

		Integer id = created.getId();
		assertNotNull(id);
		assertSame(created, service.getEmployeeById(id));
	}

	@Test
	void createEmployeeWithoutIdPassesNullIdToRepository() {
		service.createEmployee(new Employee());

		assertEquals(1, idsReceivedBySave.size());
		assertNull(idsReceivedBySave.get(0));
	}

	@Test
	void createdEmployeeIdCanBeUsedToUpdateAndDeleteTheEmployee() {
		Employee created = service.createEmployee(new Employee());
		Employee changes = new Employee();
		changes.setFirstName("Jane");
		changes.setLastName("Roe");
		changes.setEmail("jroe@xyz.com");

		Employee updated = service.updateEmployee(created.getId(), changes);
		assertEquals(created.getId(), updated.getId());
		assertEquals("Jane", updated.getFirstName());

		service.deleteEmployeeById(created.getId());
		assertFalse(store.containsKey(created.getId()));
	}

	@Test
	void getEmployeeByIdRejectsIdsThatDoNotExist() {
		service.createEmployee(new Employee());
		for (int id : new int[]{0, -1, Integer.MAX_VALUE}) {
			RuntimeException error = assertThrows(RuntimeException.class, () -> service.getEmployeeById(id));
			assertEquals("Employee not found with the id: " + id, error.getMessage());
		}
	}

	private EmployeeRepository inMemoryRepository() {
		return (EmployeeRepository) Proxy.newProxyInstance(
				EmployeeRepository.class.getClassLoader(),
				new Class<?>[]{EmployeeRepository.class},
				(proxy, method, args) -> switch (method.getName()) {
					case "findById" -> Optional.ofNullable(store.get((Integer) args[0]));
					case "findAll" -> new ArrayList<>(store.values());
					case "save" -> save((Employee) args[0]);
					case "delete" -> {
						store.remove(((Employee) args[0]).getId());
						yield null;
					}
					case "toString" -> "InMemoryEmployeeRepository";
					case "hashCode" -> System.identityHashCode(proxy);
					case "equals" -> proxy == args[0];
					default -> throw new UnsupportedOperationException(method.getName());
				});
	}

	private Employee save(Employee employee) {
		idsReceivedBySave.add(employee.getId());
		if (employee.getId() == null) {
			employee.setId(nextId++);
		}
		store.put(employee.getId(), employee);
		return employee;
	}
}
