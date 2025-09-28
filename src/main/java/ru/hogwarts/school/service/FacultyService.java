package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import java.util.Collection;
import java.util.List;

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
        return facultyRepository.findById(id).orElse(null);
    }

    public Faculty editFaculty(Faculty faculty) {
        if (!facultyRepository.existsById(faculty.getId())) {
            return null;
        }
        return facultyRepository.save(faculty);
    }

    public Faculty deleteFaculty(long id) {
        Faculty faculty = findFaculty(id);
        if (faculty != null) {
            facultyRepository.deleteById(id);
        }
        return faculty;
    }

    public Collection<Faculty> findByColor(String color) {
        return facultyRepository.findAll().stream()
                .filter(f -> f.getColor().equals(color))
                .toList();
    }

    public Collection<Faculty> findByNameOrColorIgnoreCase(String param) {
        return facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(param, param);
    }

    public Collection<Student> getStudentsByFacultyId(long facultyId) {
        Faculty faculty = findFaculty(facultyId);
        return faculty != null ? faculty.getStudents() : null;
    }
}