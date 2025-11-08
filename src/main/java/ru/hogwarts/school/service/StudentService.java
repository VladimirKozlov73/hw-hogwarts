package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.EntityNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;

@Service
public class StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository studentRepository;
    private final FacultyService facultyService;

    private final Object lock = new Object();

    @Autowired
    public StudentService(StudentRepository studentRepository, FacultyService facultyService) {
        this.studentRepository = studentRepository;
        this.facultyService = facultyService;
    }

    public Student addStudent(String name, int age, String facultyParam) {
        logger.info("Was invoked method for add student");
        logger.debug("Parameters: name={}, age={}, facultyParam={}", name, age, facultyParam);

        Faculty faculty = facultyService.findFacultyByNameOrColorIgnoreCase(facultyParam);
        if (faculty == null) {
            logger.error("There is no faculty with name or color = {}", facultyParam);
            throw new EntityNotFoundException(
                    "Факультет с именем или цветом '" + facultyParam + "' не найден");
        }
        Student student = new Student();
        student.setName(name);
        student.setAge(age);
        student.setFaculty(faculty);
        return studentRepository.save(student);
    }

    public Student findStudent(long id) {
        logger.info("Was invoked method for find student");
        logger.debug("Searching student with id={}", id);

        return studentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("There is no student with id = {}", id);
                    return new EntityNotFoundException("Студент с id=" + id + " не найден");
                });
    }

    public Student editStudent(Long id, String name, int age, String facultyParam) {
        logger.info("Was invoked method for edit student");
        logger.debug("Editing student id={}, name={}, age={}, facultyParam={}", id, name, age, facultyParam);

        if (!studentRepository.existsById(id)) {
            logger.error("Student with id={} not found for update", id);
            throw new EntityNotFoundException("Студент с id=" + id + " не найден для обновления");
        }
        Faculty faculty = facultyService.findFacultyByNameOrColorIgnoreCase(facultyParam);
        if (faculty == null) {
            logger.error("Faculty with name or color '{}' not found during student edit", facultyParam);
            throw new EntityNotFoundException(
                    "Факультет с именем или цветом '" + facultyParam + "' не найден");
        }
        Student existing = findStudent(id);
        existing.setName(name);
        existing.setAge(age);
        existing.setFaculty(faculty);
        return studentRepository.save(existing);
    }

    public void deleteStudent(long id) {
        logger.info("Was invoked method for delete student");
        logger.debug("Deleting student with id={}", id);

        findStudent(id);
        studentRepository.deleteById(id);
    }

    public Collection<Student> findByAge(int age) {
        logger.info("Was invoked method for find students by age");
        logger.debug("Searching students with age={}", age);

        return studentRepository.findAll().stream()
                .filter(s -> s.getAge() == age)
                .toList();
    }

    public Collection<Student> findByAgeBetween(int min, int max) {
        logger.info("Was invoked method for find students by age between");
        logger.debug("Searching students with age between {} and {}", min, max);

        return studentRepository.findByAgeBetween(min, max);
    }

    public Faculty getFacultyByStudentId(long studentId) {
        logger.info("Was invoked method for get faculty by student id");
        logger.debug("Fetching faculty for student id={}", studentId);

        Student student = findStudent(studentId);
        return student.getFaculty();
    }

    public Collection<Student> findStudentsByAge(Integer age) {
        logger.info("Was invoked method for find students by age (Integer)");
        logger.debug("Searching students with age={}", age);

        if (age == null || age <= 0) {
            logger.warn("Invalid age parameter: {}", age);
            return List.of();
        }
        return studentRepository.findAll().stream()
                .filter(s -> s.getAge() == age)
                .collect(Collectors.toList());
    }

    public Collection<Student> findStudentsByAgeBetween(int min, int max) {
        logger.info("Was invoked method for find students by age between (Integer)");
        logger.debug("Searching students with age between {} and {}", min, max);

        return studentRepository.findByAgeBetween(min, max);
    }

    public Long countAllStudents() {
        logger.info("Was invoked method for count all students");
        return studentRepository.countAllStudents();
    }

    public Double findAverageAge() {
        logger.info("Was invoked method for find average age");
        return studentRepository.findAverageAge();
    }

    public List<Student> findLastFiveStudents(Pageable pageable) {
        logger.info("Was invoked method for find last five students");
        return studentRepository.findLastFiveStudents(pageable);
    }

    public List<String> getStudentNamesStartingWith(String letter) {
        return studentRepository.findAll().stream()
                .map(Student::getName)
                .filter(name -> name != null && name.toUpperCase().startsWith(letter.toUpperCase()))
                .map(String::toUpperCase)
                .sorted()
                .collect(Collectors.toList());
    }

    public double findAverageAgeByCalculating() {
        List<Student> students = studentRepository.findAll();
        return students.stream()
                .mapToInt(Student::getAge)
                .average()
                .orElse(0.0);
    }

    public void printStudentNamesInParallel() {
        List<String> names = getStudentNames().stream()
                .limit(6)
                .toList();

        names.stream().limit(2).forEach(System.out::println);

        Thread thread1 = new Thread(() -> {
            names.stream().skip(2).limit(2).forEach(System.out::println);
        });

        Thread thread2 = new Thread(() -> {
            names.stream().skip(4).limit(2).forEach(System.out::println);
        });

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            System.out.println("Thread has been interrupted");
        }
    }

    public void printNamesSynchronized(List<String> names) {
        synchronized (lock) {
            for (String name : names) {
                System.out.println(name);
            }
        }
    }

    public void printStudentNamesSynchronized() {
        List<String> names = getStudentNames().stream()
                .limit(6)
                .toList();

        printNamesSynchronized(names.subList(0, 2));

        Thread thread1 = new Thread(() -> {
            printNamesSynchronized(names.subList(2, 4));
        });

        Thread thread2 = new Thread(() -> {
            printNamesSynchronized(names.subList(4, 6));
        });

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            System.out.println("Thread has been interrupted");
        }
    }

    private  List<String> getStudentNames() {
        return studentRepository.findAll().stream()
                .map(Student::getName)
                .toList();
    }
}
