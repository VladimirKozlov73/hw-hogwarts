package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.EntityNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final FacultyService facultyService;

    @Autowired
    public StudentService(StudentRepository studentRepository, FacultyService facultyService) {
        this.studentRepository = studentRepository;
        this.facultyService = facultyService;
    }


    public Student addStudent(String name, int age, String facultyParam) {
        Faculty faculty = facultyService.findFacultyByNameOrColorIgnoreCase(facultyParam);
        if (faculty == null) {
            throw new EntityNotFoundException(
                    "Факультет с именем или цветом '" + facultyParam + "' не найден");
        }
        Student student = new Student();
        student.setName(name);
        student.setAge(age);
        student.setFaculty(faculty);
        return studentRepository.save(student);
    }

    public Student findStudent(long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Студент с id=" + id + " не найден"));
    }

    public Student editStudent(Long id, String name, int age, String facultyParam) {
        if (!studentRepository.existsById(id)) {
            throw new EntityNotFoundException("Студент с id=" + id + " не найден для обновления");
        }
        Faculty faculty = facultyService.findFacultyByNameOrColorIgnoreCase(facultyParam);
        if (faculty == null) {
            throw new EntityNotFoundException(
                    "Факультет с именем или цветом '" + facultyParam + "' не найден");
        }
        Student existing = findStudent(id);
        existing.setName(name);
        existing.setAge(age);
        existing.setFaculty(faculty);
        return studentRepository.save(existing);
    }

    public void deleteStudent(long id) {
        findStudent(id);
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

    public Collection<Student> findStudentsByAge(Integer age) {
        if (age == null || age <= 0) {
            return List.of();
        }
        return studentRepository.findAll().stream()
                .filter(s -> s.getAge() == age)
                .collect(Collectors.toList());
    }

    public Collection<Student> findStudentsByAgeBetween(int min, int max) {
        return studentRepository.findByAgeBetween(min, max);
    }
}