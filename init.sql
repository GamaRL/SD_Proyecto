CREATE DATABASE IF NOT EXISTS db_tickets;
USE db_tickets;


-- create the users for each database
CREATE USER 'user'@'%' IDENTIFIED BY 'password';
GRANT CREATE, ALTER, INDEX, LOCK TABLES, REFERENCES, UPDATE, DELETE, DROP, SELECT, INSERT ON `db_tickets`.* TO 'user'@'%';

FLUSH PRIVILEGES;