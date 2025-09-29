package ru.hogwarts.school.dto;

import jakarta.validation.constraints.*;

public class FacultyCreateRequest {
    @NotBlank(message = "Имя факультета обязательно")
    private String name;

    @NotBlank(message = "Цвет факультета обязателен")
    private String color;

    public FacultyCreateRequest() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}