package ru.hogwarts.school;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import ru.hogwarts.school.dto.FacultyCreateRequest;
import ru.hogwarts.school.dto.FacultyEditRequest;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class FacultyControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private FacultyRepository facultyRepository;

    @BeforeEach
    void cleanDb() {
        facultyRepository.deleteAll();
    }

    @Test
    void createFaculty() {
        FacultyCreateRequest request = new FacultyCreateRequest();
        request.setName("Гриффиндор");
        request.setColor("Красный");

        ResponseEntity<Faculty> response = restTemplate.postForEntity("/faculty", request, Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Гриффиндор");
    }

    @Test
    void getFaculty() {
        Faculty saved = facultyRepository.save(new Faculty(0, "Слизерин", "Зеленый"));
        ResponseEntity<Faculty> response = restTemplate.getForEntity("/faculty/" + saved.getId(), Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("Слизерин");
    }

    @Test
    void editFaculty() {
        Faculty saved = facultyRepository.save(new Faculty(0, "Хаффлпафф", "Желтый"));
        FacultyEditRequest request = new FacultyEditRequest();
        request.setId(saved.getId());
        request.setName("Пуффендуй");
        request.setColor("Желтый");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<FacultyEditRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Faculty> response = restTemplate.exchange("/faculty", HttpMethod.PUT, entity, Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("Пуффендуй");
    }

    @Test
    void deleteFaculty() {
        Faculty saved = facultyRepository.save(new Faculty(0, "Факультет для удаления", "Белый"));
        restTemplate.delete("/faculty/" + saved.getId());
        assertThat(facultyRepository.existsById(saved.getId())).isFalse();
    }

    @Test
    void findFacultiesByColor() {
        facultyRepository.save(new Faculty(0, "Гриффиндор", "Красный"));
        facultyRepository.save(new Faculty(0, "Слизерин", "Зеленый"));
        ResponseEntity<Faculty[]> response = restTemplate.getForEntity("/faculty?color=Красный", Faculty[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void findFacultiesByNameOrColorPartial() {
        facultyRepository.save(new Faculty(0, "Гриффиндор", "Красный"));
        facultyRepository.save(new Faculty(0, "Слизерин", "Зеленый"));

        ResponseEntity<Faculty[]> response = restTemplate.getForEntity("/faculty/search?param=гри", Faculty[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody()[0].getName().toLowerCase()).contains("гри");
    }

    @Test
    void getStudentsByFacultyId() throws Exception {
        Faculty faculty = facultyRepository.save(new Faculty(0, "Пуффендуй", "Желтый"));

        ResponseEntity<String> response = restTemplate.getForEntity("/faculty/" + faculty.getId() +
                "/students", String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.getBody());
        String message = jsonNode.get("message").asText();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(message).contains("нет студентов");
    }
}
