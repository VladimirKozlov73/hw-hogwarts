package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.dto.StudentResponse;
import ru.hogwarts.school.exception.EntityNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FacultyService {

    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);

    private final FacultyRepository facultyRepository;

    @Autowired
    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty addFaculty(String name, String color) {
        logger.info("Was invoked method for add faculty");
        logger.debug("Parameters: name={}, color={}", name, color);

        Faculty faculty = new Faculty();
        faculty.setName(name);
        faculty.setColor(color);
        return facultyRepository.save(faculty);
    }

    public Faculty findFaculty(long id) {
        logger.info("Was invoked method for find faculty");
        logger.debug("Searching faculty with id={}", id);

        return facultyRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("There is no faculty with id = {}", id);
                    return new EntityNotFoundException("Факультет с id=" + id + " не найден");
                });
    }

    public Faculty editFaculty(Long id, String name, String color) {
        logger.info("Was invoked method for edit faculty");
        logger.debug("Editing faculty id={}, name={}, color={}", id, name, color);

        if (!facultyRepository.existsById(id)) {
            logger.error("Faculty with id={} not found for update", id);
            throw new EntityNotFoundException("Факультет с id=" + id + " не найден для обновления");
        }
        Faculty faculty = new Faculty();
        faculty.setId(id);
        faculty.setName(name);
        faculty.setColor(color);
        return facultyRepository.save(faculty);
    }

    public void deleteFaculty(long id) {
        logger.info("Was invoked method for delete faculty");
        logger.debug("Deleting faculty with id={}", id);

        findFaculty(id); // will log error if no faculty found
        facultyRepository.deleteById(id);
    }

    public Collection<Faculty> findByColor(String color) {
        logger.info("Was invoked method for find faculties by color");
        logger.debug("Searching faculties with color={}", color);

        return facultyRepository.findAll().stream()
                .filter(f -> f.getColor().equalsIgnoreCase(color))
                .toList();
    }

    public Collection<Faculty> findByNameOrColorIgnoreCase(String param) {
        logger.info("Was invoked method for find faculties by name or color ignoring case");
        logger.debug("Searching faculties with param={}", param);

        return facultyRepository.findByNameContainingIgnoreCaseOrColorContainingIgnoreCase(param, param);
    }

    public Collection<Student> getStudentsByFacultyId(long facultyId) {
        logger.info("Was invoked method for get students by faculty id");
        logger.debug("Fetching students for faculty id={}", facultyId);

        Faculty faculty = findFaculty(facultyId);
        return faculty.getStudents();
    }

    public StudentResponse getStudentsOrMessageByFacultyId(long facultyId) {
        logger.info("Was invoked method for get students or message by faculty id");
        logger.debug("Retrieving student list or message for faculty id={}", facultyId);

        Collection<Student> students = getStudentsByFacultyId(facultyId);
        if (students == null || students.isEmpty()) {
            logger.warn("No students found on faculty with id={}", facultyId);
            return new StudentResponse("На факультете с id=" + facultyId + " нет студентов");
        }
        return new StudentResponse(students);
    }

    public Faculty findFacultyByNameOrColorIgnoreCase(String param) {
        logger.info("Was invoked method for find faculty by name or color ignoring case");
        logger.debug("Searching faculty with param={}", param);

        var faculties = findByNameOrColorIgnoreCase(param.toLowerCase());
        return faculties.stream()
                .filter(f -> f.getName().equalsIgnoreCase(param) || f.getColor().equalsIgnoreCase(param))
                .findFirst()
                .orElse(null);
    }

    public Collection<Faculty> findFacultiesByColor(String color) {
        logger.info("Was invoked method for find faculties by color");
        logger.debug("Searching faculties with color={}", color);

        if (color == null || color.isBlank()) {
            logger.warn("Empty or null color parameter in findFacultiesByColor");
            return List.of();
        }
        return facultyRepository.findAll().stream()
                .filter(f -> f.getColor().equalsIgnoreCase(color))
                .collect(Collectors.toList());
    }

    public Collection<Faculty> findFacultiesByNameOrColorPartial(String param) {
        logger.info("Was invoked method for find faculties by name or color partial match");
        logger.debug("Searching faculties with partial parameter={}", param);

        String lowerParam = param.toLowerCase();
        return facultyRepository.findByNameContainingIgnoreCaseOrColorContainingIgnoreCase(param, param);
    }

    public String findLongestFacultyName() {
        logger.info("Was invoked method to find longest faculty name");
        String longest = facultyRepository.findAll().stream()
                .map(Faculty::getName)
                .filter(Objects::nonNull)
                .map(String::trim)
                .max(Comparator.comparingInt(String::length))
                .orElse("");
        logger.debug("Longest faculty name found: {}", longest);
        return longest;
    }
}
