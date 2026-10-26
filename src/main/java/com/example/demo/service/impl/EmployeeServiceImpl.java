package com.example.demo.service.impl;

import com.example.demo.entity.Employee;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee getEmployeeById(int id) {
        Optional<Employee> Employee = employeeRepository.findById(id);
        return Employee.orElseThrow(() -> new RuntimeException("Employee not found with the id: " + id));
    }

    @Override
    public Employee createEmployee(Employee employee) {
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setEmail("jdoe@xyz.com");
        return employeeRepository.save(employee);
    }

    @Override
    public Employee updateEmployee(int id, Employee employee) {
        Employee actualEmployee = employeeRepository.findById(id).orElseThrow(() -> new RuntimeException("Employee not found with the id: " + id));
        actualEmployee.setFirstName(employee.getFirstName());
        actualEmployee.setLastName(employee.getLastName());
        actualEmployee.setEmail(employee.getEmail());
        return employeeRepository.save(actualEmployee);
    }

    @Override
    public void deleteEmployeeById(int id) {
        Employee actualEmployee = employeeRepository.findById(id).orElseThrow(() -> new RuntimeException("Employee not found with the id: " + id));
        employeeRepository.delete(actualEmployee);
    }
}
