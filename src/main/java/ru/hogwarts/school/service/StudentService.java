package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.EntityNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student addStudent(Student student) {
        return studentRepository.save(student);
    }

    public Student findStudent(long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Студент с id=" + id + " не найден"));
    }

    public Student editStudent(Student student) {
        if (!studentRepository.existsById(student.getId())) {
            throw new EntityNotFoundException("Студент с id=" + student.getId() + " не найден для обновления");
        }
        return studentRepository.save(student);
    }

    public void deleteStudent(long id) {
        findStudent(id); // проверка
        studentRepository.deleteById(id);
    }

    public Collection<Student> findByAge(int age) {
        return studentRepository.findAll().stream()
                .filter(s -> s.getAge() == age)
                .toList();
    }

    public Collection<Student> findByAgeBetween(int min, int max) {
        return studentRepository.findByAgeBetween(min, max);
    }

    public Faculty getFacultyByStudentId(long studentId) {
        Student student = findStudent(studentId);
        return student.getFaculty();
    }
}