package ru.hogwarts.school.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.dto.FacultyCreateRequest;
import ru.hogwarts.school.dto.FacultyEditRequest;
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
    public Object createFaculty(@Valid @RequestBody FacultyCreateRequest request) {
        Faculty faculty = new Faculty();
        faculty.setName(request.getName());
        faculty.setColor(request.getColor());
        Faculty created = facultyService.addFaculty(faculty);
        return created;
    }

    @PutMapping
    public Object editFaculty(@Valid @RequestBody FacultyEditRequest request) {
        Faculty faculty = facultyService.findFaculty(request.getId());
        if (faculty == null) {
            return message("Факультет с id=" + request.getId() + " не найден для обновления");
        }
        faculty.setName(request.getName());
        faculty.setColor(request.getColor());
        Faculty edited = facultyService.editFaculty(faculty);
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
    public Object findFacultiesByNameOrColorPartial(@RequestParam String param) {
        Collection<Faculty> faculties = facultyService.findByNameOrColorIgnoreCase(param.toLowerCase());
        var result = faculties.stream()
                .filter(f -> f.getName().toLowerCase().contains(param.toLowerCase())
                        || f.getColor().toLowerCase().contains(param.toLowerCase()))
                .toList();
        if (result.isEmpty()) {
            return message("Факультеты с именем или цветом содержащим '" + param + "' не найдены");
        }
        return result;
    }

    @GetMapping("{id}/students")
    public Object getStudentsByFacultyId(@PathVariable Long id) {
        Collection<Student> students = facultyService.getStudentsByFacultyId(id);
        if (students == null || students.isEmpty()) {
            return message("На факультете с id=" + id + " нет студентов");
        }
        return students;
    }
}