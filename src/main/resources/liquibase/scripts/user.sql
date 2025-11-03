--liquibase formatted sql

--changeset VladimirKozlov73:1
CREATE INDEX IF NOT EXISTS idx_student_name ON student(name);

--changeset VladimirKozlov73:2
CREATE INDEX IF NOT EXISTS idx_faculty_name_color ON faculty(name, color);