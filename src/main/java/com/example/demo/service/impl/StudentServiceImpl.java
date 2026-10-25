package com.example.demo.service.impl;

import com.example.demo.entity.Student;
import com.example.demo.repository.StudentRepository;
import com.example.demo.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public Student getStudentById(int id) {
        Optional<Student> student = studentRepository.findById(id);
        return student.orElseThrow(() -> new RuntimeException("Student not found with the id: " + id));
    }

    @Override
    public Student createStudent(Student student) {
        student.setFirstName("John");
        student.setLastName("Doe");
        student.setEmail("jdoe@xyz.com");
        return studentRepository.save(student);
    }

    @Override
    public Student updateStudent(int id, Student student) {
        Student actualStudent = studentRepository.findById(id).orElseThrow(() -> new RuntimeException("Student not found with the id: " + id));
        actualStudent.setFirstName(student.getFirstName());
        actualStudent.setLastName(student.getLastName());
        actualStudent.setEmail(student.getEmail());
        return studentRepository.save(actualStudent);
    }

    @Override
    public void deleteStudentById(int id) {
        Student actualStudent = studentRepository.findById(id).orElseThrow(() -> new RuntimeException("Student not found with the id: " + id));
        studentRepository.delete(actualStudent);
    }
}
