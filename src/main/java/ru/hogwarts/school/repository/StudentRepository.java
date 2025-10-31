package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school.model.Student;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Pageable;


public interface StudentRepository extends JpaRepository<Student, Long> {
    Collection<Student> findByAgeBetween(int min, int max);

    @Query("select count(s) from Student s")
    Long countAllStudents();

    @Query("select avg(s.age) from Student s")
    Double findAverageAge();

    @Query(value = "select s from Student s order by s.id desc")
    List<Student> findLastFiveStudents(Pageable pageable);
}