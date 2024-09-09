CREATE DATABASE IF NOT EXISTS gym_management;
USE gym_management;

CREATE TABLE IF NOT EXISTS members (
    member_id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    mobile_number VARCHAR(15) NOT NULL,
    email VARCHAR(50) NOT NULL,
    gender VARCHAR(10) NOT NULL,
    age VARCHAR(5) NOT NULL,
    gym_timing VARCHAR(20) NOT NULL,
    salary_amount VARCHAR(10) NOT NULL
);

CREATE TABLE salary_info (
    month VARCHAR(20) NOT NULL,
    year VARCHAR(4) NOT NULL,
    total_people INT NOT NULL,
    total_salary INT NOT NULL
);

CREATE TABLE IF NOT EXISTS attendance_info (
    name VARCHAR(50),
    date VARCHAR(2),
    month VARCHAR(2),
    year VARCHAR(4),
    attendance_status CHAR(1)
);
