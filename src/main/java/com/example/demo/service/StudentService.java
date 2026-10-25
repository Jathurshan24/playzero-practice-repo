package com.example.demo.service;

import com.example.demo.entity.Student;

import java.util.List;

public interface StudentService {
    public List<Student> getAllStudents();

    public Student getStudentById(int id);

    public Student createStudent(Student student);

    public Student updateStudent(int id, Student student);

    public void deleteStudentById(int id);
}
