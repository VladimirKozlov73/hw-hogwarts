package ru.hogwarts.school.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.dto.FacultyCreateRequest;
import ru.hogwarts.school.dto.FacultyEditRequest;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.service.FacultyService;

import java.util.Collection;
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
        return facultyService.addFaculty(request.getName(), request.getColor());
    }

    @PutMapping
    public Faculty editFaculty(@Valid @RequestBody FacultyEditRequest request) {
        return facultyService.editFaculty(request.getId(), request.getName(), request.getColor());
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFaculty(@PathVariable Long id) {
        facultyService.deleteFaculty(id);
    }

    @GetMapping
    public Collection<Faculty> findFaculties(@RequestParam(required = false) String color) {
        return facultyService.findFacultiesByColor(color);
    }

    @GetMapping("/search")
    public Collection<Faculty> findFacultiesByNameOrColorPartial(@RequestParam String param) {
        return facultyService.findFacultiesByNameOrColorPartial(param);
    }

    @GetMapping("{id}/students")
    public Object getStudentsByFacultyId(@PathVariable Long id) {
        return facultyService.getStudentsOrMessageByFacultyId(id);
    }

    @GetMapping("/longestName")
    public String getLongestFacultyName() {
        return facultyService.findLongestFacultyName();
    }
}