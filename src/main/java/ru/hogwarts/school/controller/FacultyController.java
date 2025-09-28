package ru.hogwarts.school.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/faculty")
public class FacultyController {

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    private Map<String, String> message(String msg) {
        return Map.of("сообщение", msg);
    }

    @GetMapping("{id}")
    public Object getFacultyInfo(@PathVariable Long id) {
        Faculty faculty = facultyService.findFaculty(id);
        if (faculty == null) {
            return message("Факультет с id=" + id + " не найден");
        }
        return faculty;
    }

    @PostMapping
    public Object createFaculty(@RequestBody Faculty faculty) {
        Faculty created = facultyService.addFaculty(faculty);
        if (created == null) {
            return message("Ошибка при создании факультета");
        }
        return created;
    }

    @PutMapping
    public Object editFaculty(@RequestBody Faculty faculty) {
        Faculty edited = facultyService.editFaculty(faculty);
        if (edited == null) {
            return message("Факультет с id=" + faculty.getId() + " не найден для обновления");
        }
        return edited;
    }

    @DeleteMapping("{id}")
    public Object deleteFaculty(@PathVariable Long id) {
        Faculty faculty = facultyService.findFaculty(id);
        if (faculty == null) {
            return message("Факультет с id=" + id + " не найден для удаления");
        }
        facultyService.deleteFaculty(id);
        return message("Факультет с id=" + id + " успешно удален");
    }

    @GetMapping
    public Object findFaculties(@RequestParam(required = false) String color) {
        if (color != null && !color.isBlank()) {
            Collection<Faculty> faculties = facultyService.findByColor(color);
            if (faculties.isEmpty()) {
                return message("Факультеты с цветом '" + color + "' не найдены");
            }
            return faculties;
        }
        return Collections.emptyList();
    }

    @GetMapping("/search")
    public Object findFacultiesByNameOrColor(@RequestParam String param) {
        Collection<Faculty> faculties = facultyService.findByNameOrColorIgnoreCase(param);
        if (faculties.isEmpty()) {
            return message("Факультеты с именем или цветом '" + param + "' не найдены");
        }
        return faculties;
    }

    @GetMapping("{id}/students")
    public Object getStudentsByFacultyId(@PathVariable Long id) {
        Collection<Student> students = facultyService.getStudentsByFacultyId(id);
        if (students == null || students.isEmpty()) {
            return message("Студенты для факультета с id=" + id + " не найдены");
        }
        return students;
    }
}