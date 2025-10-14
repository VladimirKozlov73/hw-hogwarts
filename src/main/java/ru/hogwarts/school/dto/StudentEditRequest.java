package ru.hogwarts.school.dto;

import jakarta.validation.constraints.*;

public class StudentEditRequest {
    @NotNull(message = "ID студента обязателен")
    private Long id;

    @NotBlank(message = "Имя студента обязательно")
    private String name;

    @Min(value = 1, message = "Возраст должен быть положительным")
    private int age;

    @NotBlank(message = "Факультет (имя или цвет) обязателен")
    private String faculty;

    public StudentEditRequest() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }
}