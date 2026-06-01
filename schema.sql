CREATE DATABASE IF NOT EXISTS tracker_db;
USE tracker_db;

CREATE TABLE IF NOT EXISTS expenses (
                                        id       BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        title    VARCHAR(255)   NOT NULL,
    amount   DOUBLE         NOT NULL,
    type     VARCHAR(50),
    category VARCHAR(50),
    date     DATE
    );