package ru.hogwarts.school.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Collection;
import ru.hogwarts.school.model.Student;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentResponse {

    private Collection<Student> students;
    private String message;

    public StudentResponse(Collection<Student> students) {
        this.students = students;
        this.message = null;
    }

    public StudentResponse(String message) {
        this.students = null;
        this.message = message;
    }

    public Collection<Student> getStudents() {
        return students;
    }

    public String getMessage() {
        return message;
    }
}
