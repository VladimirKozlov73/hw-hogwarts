package ru.hogwarts.school.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.dto.StudentCreateRequest;
import ru.hogwarts.school.dto.StudentEditRequest;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;

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
        return studentService.addStudent(request.getName(), request.getAge(), request.getFaculty());
    }

    @PutMapping
    public Student editStudent(@Valid @RequestBody StudentEditRequest request) {
        return studentService.editStudent(request.getId(), request.getName(), request.getAge(), request.getFaculty());
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
    }

    @GetMapping
    public Collection<Student> findStudents(@RequestParam(required = false) Integer age) {
        return studentService.findStudentsByAge(age);
    }

    @GetMapping("/ageBetween")
    public Collection<Student> findStudentsByAgeBetween(@RequestParam int min, @RequestParam int max) {
        return studentService.findStudentsByAgeBetween(min, max);
    }

    @GetMapping("{id}/faculty")
    public Faculty getFacultyByStudentId(@PathVariable Long id) {
        return studentService.getFacultyByStudentId(id);
    }

    @GetMapping("/count")
    public Long getStudentsCount() {
        return studentService.countAllStudents();
    }

    @GetMapping("/averageAge")
    public Double getAverageAge() {
        return studentService.findAverageAge();
    }

    @GetMapping("/lastFive")
    public List<Student> getLastFiveStudents() {
        return studentService.findLastFiveStudents(PageRequest.of(0, 5));
    }

    @GetMapping("/names/startWith")
    public List<String> getStudentNamesStartWith(@RequestParam String letter) {
        return studentService.getStudentNamesStartingWith(letter);
    }

    @GetMapping("/averageAgeCalculated")
    public Double getAverageAgeCalculated() {
        return studentService.findAverageAgeByCalculating();
    }

    @GetMapping("/print-parallel")
    public void printNamesParallel() {
        studentService.printStudentNamesInParallel();
    }

    @GetMapping("/print-synchronized")
    public void printSynchronized() {
        studentService.printStudentNamesSynchronized();
    }
}
