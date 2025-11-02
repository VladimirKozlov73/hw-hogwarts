CREATE TABLE car (
    id SERIAL PRIMARY KEY,
    make VARCHAR(255),
    model VARCHAR(255),
    price NUMERIC(15, 2)
);

CREATE TABLE person (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    age INT,
    has_license BOOLEAN,
    car_id INT,
    CONSTRAINT fk_car FOREIGN KEY (car_id) REFERENCES car(id)
);