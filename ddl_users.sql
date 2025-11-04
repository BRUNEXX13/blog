CREATE TABLE users
(
    id         UUID                        NOT NULL,
    first_name VARCHAR(255),
    last_name  VARCHAR(255),
    email      VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    birth_date date,
    CONSTRAINT pk_users PRIMARY KEY (id)
);