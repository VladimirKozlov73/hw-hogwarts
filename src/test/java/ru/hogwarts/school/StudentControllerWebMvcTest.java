package ru.hogwarts.school;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.dto.StudentCreateRequest;
import ru.hogwarts.school.dto.StudentEditRequest;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.service.StudentService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
public class StudentControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @MockBean
    private FacultyService facultyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createStudent() throws Exception {
        StudentCreateRequest request = new StudentCreateRequest();
        request.setName("Созданный студент");
        request.setAge(19);
        request.setFaculty("Гриффиндор");

        Faculty faculty = new Faculty(1, "Гриффиндор", "Красный", null);
        Student student = new Student(1, request.getName(), request.getAge(), faculty);

        Mockito.when(studentService.addStudent(request.getName(), request.getAge(), request.getFaculty()))
                .thenReturn(student);

        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Созданный студент"));
    }

    @Test
    void getStudent() throws Exception {
        long id = 1L;
        Faculty faculty = new Faculty(1, "Гриффиндор", "Красный", null);
        Student student = new Student(id, "Полученный студент", 20, faculty);

        Mockito.when(studentService.findStudent(id)).thenReturn(student);

        mockMvc.perform(get("/student/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Полученный студент"));
    }

    @Test
    void editStudent() throws Exception {
        StudentEditRequest request = new StudentEditRequest();
        request.setId(1L);
        request.setName("Изменённый студент");
        request.setAge(21);
        request.setFaculty("[Пуффендуй]");

        Faculty faculty = new Faculty(2, "Пуффендуй", "Желтый", null);
        Student student = new Student(1, request.getName(), request.getAge(), faculty);

        Mockito.when(studentService.editStudent(request.getId(), request.getName(), request.getAge(), request.getFaculty()))
                .thenReturn(student);

        mockMvc.perform(put("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Изменённый студент"));
    }

    @Test
    void deleteStudent() throws Exception {
        long id = 1L;

        mockMvc.perform(delete("/student/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void findStudentsByAge() throws Exception {
        int age = 18;
        Faculty faculty = new Faculty(1, "Гриффиндор", "Красный", null);
        Student student = new Student(1, "Студент", age, faculty);

        Mockito.when(studentService.findStudentsByAge(age)).thenReturn(List.of(student));

        mockMvc.perform(get("/student").param("age", String.valueOf(age)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Студент"));
    }

    @Test
    void findStudentsByAgeBetween() throws Exception {
        int minAge = 18;
        int maxAge = 25;
        Faculty faculty = new Faculty(1, "Гриффиндор", "Красный", null);
        Student student1 = new Student(1, "Студент младший", 19, faculty);
        Student student2 = new Student(2, "Студент старший", 23, faculty);

        Mockito.when(studentService.findStudentsByAgeBetween(minAge, maxAge))
                .thenReturn(List.of(student1, student2));

        mockMvc.perform(get("/student/ageBetween")
                        .param("min", String.valueOf(minAge))
                        .param("max", String.valueOf(maxAge)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Студент младший"))
                .andExpect(jsonPath("$[1].name").value("Студент старший"));
    }

    @Test
    void getFacultyByStudentId() throws Exception {
        Long studentId = 1L;
        Faculty faculty = new Faculty(1, "Гриффиндор", "Красный", null);

        Mockito.when(studentService.getFacultyByStudentId(studentId)).thenReturn(faculty);

        mockMvc.perform(get("/student/{id}/faculty", studentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Гриффиндор"))
                .andExpect(jsonPath("$.color").value("Красный"));
    }
}