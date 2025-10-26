package ru.hogwarts.school;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import ru.hogwarts.school.dto.StudentCreateRequest;
import ru.hogwarts.school.dto.StudentEditRequest;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.service.StudentService;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class StudentControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private StudentService studentService;

    @Autowired
    private FacultyService facultyService;

    @BeforeEach
    void cleanDb() {
        studentRepository.deleteAll();
        facultyRepository.deleteAll();
    }

    @Test
    void contextLoads() {
        assertThat(studentService).isNotNull();
        assertThat(facultyService).isNotNull();
        System.out.println("Сервер запущен на порту: " + port);
    }

    @Test
    void createStudent() {
        Faculty faculty = new Faculty();
        faculty.setName("Гриффендор");
        faculty.setColor("Красный");
        faculty = facultyRepository.save(faculty);

        StudentCreateRequest request = new StudentCreateRequest();
        request.setName("Студент");
        request.setAge(20);
        request.setFaculty("Гриффендор");

        ResponseEntity<Student> response = restTemplate.postForEntity("/student", request, Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Студент");
    }

    @Test
    void getStudent() {
        Student saved = studentRepository.save(new Student(0, "Студент", 21));
        ResponseEntity<Student> response = restTemplate.getForEntity("/student/" + saved.getId(), Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("Студент");
    }

    @Test
    void editStudent() {
        Faculty faculty = new Faculty();
        faculty.setName("Пуффендор");
        faculty.setColor("Жёлтый");
        faculty = facultyRepository.save(faculty);

        Student saved = new Student();
        saved.setName("Изменяемый студент");
        saved.setAge(22);
        saved.setFaculty(faculty);
        saved = studentRepository.save(saved);

        StudentEditRequest request = new StudentEditRequest();
        request.setId(saved.getId());
        request.setName("Измененный студент");
        request.setAge(23);
        request.setFaculty("Пуффендор");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<StudentEditRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Student> response = restTemplate.exchange("/student", HttpMethod.PUT, entity, Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("Измененный студент");
        assertThat(response.getBody().getAge()).isEqualTo(23);
    }

    @Test
    void deleteStudent() {
        Student saved = studentRepository.save(new Student(0, "Удаляемый студент", 25));
        restTemplate.delete("/student/" + saved.getId());
        assertThat(studentRepository.existsById(saved.getId())).isFalse();
    }

    @Test
    void findStudentsByAge() {
        studentRepository.save(new Student(0, "Студент молодой", 18));
        studentRepository.save(new Student(0, "Студент старый", 30));

        ResponseEntity<Student[]> response = restTemplate.getForEntity("/student?age=18", Student[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody()[0].getAge()).isEqualTo(18);
    }

    @Test
    void findStudentsByAgeBetween() {
        Faculty faculty = new Faculty();
        faculty.setName("Гриффендор");
        faculty.setColor("Красный");
        faculty = facultyRepository.save(faculty);

        Student student1 = new Student();
        student1.setName("Студент не младше");
        student1.setAge(19);
        student1.setFaculty(faculty);
        student1 = studentRepository.save(student1);

        Student student2 = new Student();
        student2.setName("Студент не старше");
        student2.setAge(23);
        student2.setFaculty(faculty);
        student2 = studentRepository.save(student2);

        ResponseEntity<Student[]> response = restTemplate.getForEntity(
                "/student/ageBetween?min=18&max=25",
                Student[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()[0].getName()).isEqualTo("Студент не младше");
        assertThat(response.getBody()[1].getName()).isEqualTo("Студент не старше");
    }

    @Test
    void getFacultyByStudentId() {
        Faculty faculty = new Faculty();
        faculty.setName("Гриффендор");
        faculty.setColor("Красный");
        faculty = facultyRepository.save(faculty);

        Student student = new Student();
        student.setName("Студент");
        student.setAge(20);
        student.setFaculty(faculty);
        student = studentRepository.save(student);

        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                "/student/" + student.getId() + "/faculty",
                Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("Гриффендор");
        assertThat(response.getBody().getColor()).isEqualTo("Красный");
    }
}