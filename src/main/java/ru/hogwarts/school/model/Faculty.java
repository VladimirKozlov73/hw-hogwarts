package ru.hogwarts.school.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.Collection;
import java.util.Objects;

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "color"})
)
public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String color;

    @OneToMany(mappedBy = "faculty", fetch = FetchType.LAZY)
    @JsonManagedReference
    private Collection<Student> students;

    public Faculty() {
    }

    public Faculty(long id, String name, String color, Collection<Student> students) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.students = students;
    }

    public Faculty(long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Faculty faculty = (Faculty) o;
        return id == faculty.id && Objects.equals(name, faculty.name) && Objects.equals(color, faculty.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }

    @Override
    public String toString() {
        return "Faculty{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", students=" + (students != null ? students.size() : 0) +
                '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public Collection<Student> getStudents() {
        return students;
    }

    public void setStudents(Collection<Student> students) {
        this.students = students;
    }
}