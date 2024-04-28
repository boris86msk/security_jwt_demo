
CREATE TABLE persons (
    id       SERIAL PRIMARY KEY,
    email    VARCHAR(50) NOT NULL UNIQUE ,
    password VARCHAR(120) NOT NULL,
    username VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE roles (
    id   SERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE persons_roles
(
    role_id   INT REFERENCES roles(id),
    person_id INT REFERENCES persons(id)
);