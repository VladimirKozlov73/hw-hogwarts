SELECT s.name, s.age, f.name AS faculty_name
FROM student s
INNER JOIN faculty f ON s.faculty_id = f.id;

SELECT s.name, s.age, f.name AS faculty_name
FROM student s
INNER JOIN faculty f ON s.faculty_id = f.id
INNER JOIN avatar a ON a.student_id = s.id;