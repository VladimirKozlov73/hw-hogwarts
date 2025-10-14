package ru.hogwarts.school.dto;

import jakarta.validation.constraints.*;

public class StudentCreateRequest {
    @NotBlank(message = "Имя студента обязательно")
    private String name;

    @Min(value = 1, message = "Возраст должен быть положительным")
    private int age;

    @NotBlank(message = "Факультет (имя или цвет) обязателен")
    private String faculty;

    public StudentCreateRequest() {}

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