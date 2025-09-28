package ru.hogwarts.school.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    private Map<String, String> message(String msg) {
        return Map.of("сообщение", msg);
    }

    @GetMapping("{id}")
    public Object getStudentInfo(@PathVariable Long id) {
        Student student = studentService.findStudent(id);
        if (student == null) {
            return message("Студент с id=" + id + " не найден");
        }
        if (student.getFaculty() != null) {
            return student;
        }
        return student;
    }

    @PostMapping
    public Object createStudent(@RequestBody Student student) {
        Student created = studentService.addStudent(student);
        if (created == null) {
            return message("Ошибка при создании студента");
        }
        return created;
    }

    @PutMapping
    public Object editStudent(@RequestBody Student student) {
        Student edited = studentService.editStudent(student);
        if (edited == null) {
            return message("Студент с id=" + student.getId() + " не найден для обновления");
        }
        return edited;
    }

    @DeleteMapping("{id}")
    public Object deleteStudent(@PathVariable Long id) {
        Student student = studentService.findStudent(id);
        if (student == null) {
            return message("Студент с id=" + id + " не найден для удаления");
        }
        studentService.deleteStudent(id);
        return message("Студент с id=" + id + " успешно удален");
    }

    @GetMapping
    public Object findStudents(@RequestParam(required = false) Integer age) {
        if (age != null && age > 0) {
            var students = studentService.findByAge(age);
            if (students.isEmpty()) {
                return Map.of("сообщение", "Студенты с возрастом " + age + " не найдены");
            }
            return students;
        }
        return Collections.emptyList();
    }

    @GetMapping("/ageBetween")
    public Object findStudentsByAgeBetween(@RequestParam int min, @RequestParam int max) {
        Collection<Student> students = studentService.findByAgeBetween(min, max);
        if (students.isEmpty()) {
            return message("Студенты в возрасте от " + min + " до " + max + " не найдены");
        }
        return students;
    }

    @GetMapping("{id}/faculty")
    public Object getFacultyByStudentId(@PathVariable Long id) {
        Faculty faculty = studentService.getFacultyByStudentId(id);
        if (faculty == null) {
            return message("На факультете нет студента с id=" + id);
        }
        return faculty;
    }
}
