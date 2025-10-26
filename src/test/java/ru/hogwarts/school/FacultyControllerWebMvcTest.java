package ru.hogwarts.school;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.dto.FacultyCreateRequest;
import ru.hogwarts.school.dto.FacultyEditRequest;
import ru.hogwarts.school.dto.StudentResponse;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FacultyController.class)
public class FacultyControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacultyService facultyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createFaculty() throws Exception {
        FacultyCreateRequest request = new FacultyCreateRequest();
        request.setName("Гриффиндор");
        request.setColor("Красный");

        Faculty faculty = new Faculty(1, request.getName(), request.getColor());

        Mockito.when(facultyService.addFaculty(request.getName(), request.getColor()))
                .thenReturn(faculty);

        mockMvc.perform(post("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Гриффиндор"));
    }

    @Test
    void getFaculty() throws Exception {
        long id = 1L;
        Faculty faculty = new Faculty(id, "Слизерин", "Зеленый");

        Mockito.when(facultyService.findFaculty(id)).thenReturn(faculty);

        mockMvc.perform(get("/faculty/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Слизерин"));
    }

    @Test
    void editFaculty() throws Exception {
        FacultyEditRequest request = new FacultyEditRequest();
        request.setId(1L);
        request.setName("Пуффендуй");
        request.setColor("Желтый");

        Faculty faculty = new Faculty(1, request.getName(), request.getColor());

        Mockito.when(facultyService.editFaculty(request.getId(), request.getName(), request.getColor()))
                .thenReturn(faculty);

        mockMvc.perform(put("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Пуффендуй"));
    }

    @Test
    void deleteFaculty() throws Exception {
        long id = 1L;

        mockMvc.perform(delete("/faculty/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void findFacultiesByColor() throws Exception {
        String color = "Красный";
        Faculty faculty = new Faculty(1, "Гриффиндор", color);

        Mockito.when(facultyService.findFacultiesByColor(color)).thenReturn(List.of(faculty));

        mockMvc.perform(get("/faculty").param("color", color))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Гриффиндор"));
    }

    @Test
    void findFacultiesByNameOrColorPartial() throws Exception {
        String param = "гри";
        Faculty faculty = new Faculty(1, "Гриффиндор", "Красный");

        Mockito.when(facultyService.findFacultiesByNameOrColorPartial(param)).thenReturn(List.of(faculty));

        mockMvc.perform(get("/faculty/search").param("param", param))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Гриффиндор"));
    }

    @Test
    void getStudentsByFacultyId() throws Exception {
        long facultyId = 1L;
        Mockito.when(facultyService.getStudentsOrMessageByFacultyId(facultyId))
                .thenReturn(new StudentResponse("На факультете с id=1 нет студентов"));

        mockMvc.perform(get("/faculty/{id}/students", facultyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("нет студентов")));
    }
}