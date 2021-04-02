create database springboot;
use springboot;
create table employees(employeeCode bigint auto_increment primary key, name varchar(60) not null, location varchar(60) not null, email varchar(100) not null unique, dateOfBirth date not null);
desc employees;