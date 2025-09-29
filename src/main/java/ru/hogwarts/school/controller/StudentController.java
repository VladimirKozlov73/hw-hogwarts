package ru.hogwarts.school.controller;

import org.springframework.web.bind.annotation.*;
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
    public Object getStudentInfo(@PathVariable Long id) {
        Student student = studentService.findStudent(id);
        if (student == null) {
            return message("Студент с id=" + id + " не найден");
        }
        return student;
    }

    @PostMapping
    public Object createStudent(@RequestBody Map<String, Object> request) {
        try {
            String name = (String) request.get("name");
            Integer age = (Integer) request.get("age");
            String facultyNameOrColor = (String) request.get("faculty");

            if (name == null || age == null || facultyNameOrColor == null) {
                return message("Поля name, age и faculty обязательны");
            }
            Faculty faculty = findFacultyByNameOrColorIgnoreCase(facultyNameOrColor);
            if (faculty == null) {
                return message("Факультет с именем или цветом '" + facultyNameOrColor + "' не найден");
            }
            Student student = new Student();
            student.setName(name);
            student.setAge(age);
            student.setFaculty(faculty);
            Student created = studentService.addStudent(student);
            return created;
        } catch (ClassCastException e) {
            return message("Ошибка формата входных данных");
        }
    }

    @PutMapping
    public Object editStudent(@RequestBody Map<String, Object> request) {
        try {
            Long id = ((Number) request.get("id")).longValue();
            String name = (String) request.get("name");
            Integer age = (Integer) request.get("age");
            String facultyNameOrColor = (String) request.get("faculty");

            if (id == null || name == null || age == null || facultyNameOrColor == null) {
                return message("Поля id, name, age и faculty обязательны");
            }
            Faculty faculty = findFacultyByNameOrColorIgnoreCase(facultyNameOrColor);
            if (faculty == null) {
                return message("Факультет с именем или цветом '" + facultyNameOrColor + "' не найден");
            }
            Student existing = studentService.findStudent(id);
            if (existing == null) {
                return message("Студент с id=" + id + " не найден для обновления");
            }
            existing.setName(name);
            existing.setAge(age);
            existing.setFaculty(faculty);
            Student edited = studentService.editStudent(existing);
            return edited;
        } catch (ClassCastException | NullPointerException e) {
            return message("Ошибка формата входных данных");
        }
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

    private Faculty findFacultyByNameOrColorIgnoreCase(String param) {
        var faculties = facultyService.findByNameOrColorIgnoreCase(param.toLowerCase());
        return faculties.stream()
                .filter(f -> f.getName().equalsIgnoreCase(param) || f.getColor().equalsIgnoreCase(param))
                .findFirst()
                .orElse(null);
    }
}
