package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.EntityNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Collection;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;

    @Autowired
    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty addFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    public Faculty findFaculty(long id) {
        return facultyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Факультет с id=" + id + " не найден"));
    }

    public Faculty editFaculty(Faculty faculty) {
        if (!facultyRepository.existsById(faculty.getId())) {
            throw new EntityNotFoundException("Факультет с id=" + faculty.getId() + " не найден для обновления");
        }
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

    public Faculty findFacultyByNameOrColorIgnoreCase(String param) {
        var faculties = findByNameOrColorIgnoreCase(param.toLowerCase());
        return faculties.stream()
                .filter(f -> f.getName().equalsIgnoreCase(param) || f.getColor().equalsIgnoreCase(param))
                .findFirst()
                .orElse(null);
    }
}