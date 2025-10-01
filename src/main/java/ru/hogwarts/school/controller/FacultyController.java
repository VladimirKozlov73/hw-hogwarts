package ru.hogwarts.school.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
    public Faculty getFacultyInfo(@PathVariable Long id) {
        return facultyService.findFaculty(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Faculty createFaculty(@Valid @RequestBody FacultyCreateRequest request) {
        Faculty faculty = new Faculty();
        faculty.setName(request.getName());
        faculty.setColor(request.getColor());
        return facultyService.addFaculty(faculty);
    }

    @PutMapping
    public Faculty editFaculty(@Valid @RequestBody FacultyEditRequest request) {
        Faculty faculty = new Faculty();
        faculty.setId(request.getId());
        faculty.setName(request.getName());
        faculty.setColor(request.getColor());
        return facultyService.editFaculty(faculty);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFaculty(@PathVariable Long id) {
        facultyService.deleteFaculty(id);
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