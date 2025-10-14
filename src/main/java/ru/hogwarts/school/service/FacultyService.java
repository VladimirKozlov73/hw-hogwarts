package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.dto.StudentResponse;
import ru.hogwarts.school.exception.EntityNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;

    @Autowired
    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty addFaculty(String name, String color) {
        Faculty faculty = new Faculty();
        faculty.setName(name);
        faculty.setColor(color);
        return facultyRepository.save(faculty);
    }

    public Faculty findFaculty(long id) {
        return facultyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Факультет с id=" + id + " не найден"));
    }

    public Faculty editFaculty(Long id, String name, String color) {
        if (!facultyRepository.existsById(id)) {
            throw new EntityNotFoundException("Факультет с id=" + id + " не найден для обновления");
        }
        Faculty faculty = new Faculty();
        faculty.setId(id);
        faculty.setName(name);
        faculty.setColor(color);
        return facultyRepository.save(faculty);
    }

    public void deleteFaculty(long id) {
        findFaculty(id);
        facultyRepository.deleteById(id);
    }

    public Collection<Faculty> findByColor(String color) {
        return facultyRepository.findAll().stream()
                .filter(f -> f.getColor().equalsIgnoreCase(color))
                .toList();
    }

    public Collection<Faculty> findByNameOrColorIgnoreCase(String param) {
        return facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(param, param);
    }

    public Collection<Student> getStudentsByFacultyId(long facultyId) {
        Faculty faculty = findFaculty(facultyId);
        return faculty.getStudents();
    }

    public StudentResponse getStudentsOrMessageByFacultyId(long facultyId) {
        Collection<Student> students = getStudentsByFacultyId(facultyId);
        if (students == null || students.isEmpty()) {
            return new StudentResponse("На факультете с id=" + facultyId + " нет студентов");
        }
        return new StudentResponse(students);
    }

    public Faculty findFacultyByNameOrColorIgnoreCase(String param) {
        var faculties = findByNameOrColorIgnoreCase(param.toLowerCase());
        return faculties.stream()
                .filter(f -> f.getName().equalsIgnoreCase(param) || f.getColor().equalsIgnoreCase(param))
                .findFirst()
                .orElse(null);
    }

    public Collection<Faculty> findFacultiesByColor(String color) {
        if (color == null || color.isBlank()) {
            return List.of();
        }
        return facultyRepository.findAll().stream()
                .filter(f -> f.getColor().equalsIgnoreCase(color))
                .collect(Collectors.toList());
    }

    public Collection<Faculty> findFacultiesByNameOrColorPartial(String param) {
        String lowerParam = param.toLowerCase();
        return facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(lowerParam, lowerParam).stream()
                .filter(f -> f.getName().toLowerCase().contains(lowerParam)
                        || f.getColor().toLowerCase().contains(lowerParam))
                .collect(Collectors.toList());
    }
}