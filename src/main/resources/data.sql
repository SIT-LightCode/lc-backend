-- Insert data into the Tag table
use lightcode;
INSERT INTO Tag (topic, description) VALUES
                                         ('Java', 'Java programming language'),
                                         ('Spring', 'Spring Framework'),
                                         ('SQL', 'Structured Query Language')
-- add more rows as needed
;

-- Insert data into the Lesson table
INSERT INTO Lesson (tag_id, name, content) VALUES
                                               (1, 'Introduction to Java', 'This lesson covers the basics of Java programming.'),
                                               (1, 'Object-Oriented Programming', 'Learn about OOP concepts in Java.'),
                                               (2, 'Spring Boot Overview', 'Introduction to Spring Boot framework.'),
                                               (2, 'Dependency Injection', 'Understanding the concept of dependency injection in Spring.'),
                                               (3, 'SQL Basics', 'Introduction to basic SQL commands.')
;
