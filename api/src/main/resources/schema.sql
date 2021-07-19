-- create database

DROP TABLE IF EXISTS books CASCADE;

-- V1
CREATE TABLE books (
  book_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id VARCHAR(30) NOT NULL,
  title VARCHAR(250) NOT NULL,
  author VARCHAR(250) NOT NULL,
  engine_version INT NOT NULL,
  create_time TIMESTAMP NOT NULL,
  raw_input CLOB DEFAULT NULL, -- MySQL replace CLOB by LONGTEXT
  storydom CLOB DEFAULT NULL, -- MySQL replace CLOB by LONGTEXT
  annotated_storydom CLOB DEFAULT NULL, -- MySQL replace CLOB by LONGTEXT
  annotation_complete_time TIMESTAMP DEFAULT NULL,
  is_report_available BOOL DEFAULT FALSE,
  message VARCHAR(1000) DEFAULT NULL
);

-- V2
ALTER TABLE books
ADD user_email VARCHAR(256);

-- V3
ALTER TABLE books
ADD percent_complete INT DEFAULT NULL;
ALTER TABLE books
ADD remain_mins INT DEFAULT NULL;

-- V4
ALTER TABLE books
ADD annotation_start_time TIMESTAMP DEFAULT NULL;
ALTER TABLE books
ADD validated_by_user BOOL DEFAULT FALSE;
-- update books set annotation_start_time = create_time, validated_by_user = true where annotation_start_time is null;
