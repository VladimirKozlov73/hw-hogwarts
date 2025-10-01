package ru.hogwarts.school.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.dto.StudentCreateRequest;
import ru.hogwarts.school.dto.StudentEditRequest;
import ru.hogwarts.school.exception.EntityNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;
    private final FacultyService facultyService;

    public StudentController(StudentService studentService, FacultyService facultyService) {
        this.studentService = studentService;
        this.facultyService = facultyService;
    }

    private Map<String, String> message(String msg) {
        return Map.of("сообщение", msg);
    }

    @GetMapping("{id}")
    public Student getStudentInfo(@PathVariable Long id) {
        return studentService.findStudent(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Student createStudent(@Valid @RequestBody StudentCreateRequest request) {
        Faculty faculty = facultyService.findFacultyByNameOrColorIgnoreCase(request.getFaculty());
        if (faculty == null) {
            throw new EntityNotFoundException(
                    "Факультет с именем или цветом '" + request.getFaculty() + "' не найден");
        }
        Student student = new Student();
        student.setName(request.getName());
        student.setAge(request.getAge());
        student.setFaculty(faculty);
        return studentService.addStudent(student);
    }

    @PutMapping
    public Student editStudent(@Valid @RequestBody StudentEditRequest request) {
        Faculty faculty = facultyService.findFacultyByNameOrColorIgnoreCase(request.getFaculty());
        if (faculty == null) {
            throw new EntityNotFoundException(
                    "Факультет с именем или цветом '" + request.getFaculty() + "' не найден");
        }
        Student existing = studentService.findStudent(request.getId());
        existing.setName(request.getName());
        existing.setAge(request.getAge());
        existing.setFaculty(faculty);
        return studentService.editStudent(existing);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
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
    public Faculty getFacultyByStudentId(@PathVariable Long id) {
        return studentService.getFacultyByStudentId(id);
    }
}
