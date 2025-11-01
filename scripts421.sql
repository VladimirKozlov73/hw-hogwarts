ALTER TABLE faculty
ADD CONSTRAINT unique_faculty_name_color UNIQUE (name, color);

ALTER TABLE student
ALTER COLUMN name SET NOT NULL;

ALTER TABLE student
ADD CONSTRAINT unique_student_name UNIQUE (name);

ALTER TABLE student
ADD CONSTRAINT check_student_age CHECK (age >= 16);

ALTER TABLE student
ALTER COLUMN age SET DEFAULT 20;