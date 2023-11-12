-- Insert data into the Tag table
use lightcode;
INSERT INTO Tag (id, topic, description) VALUES
                                         (1, 'Java', 'Java programming language'),
                                         (2, 'Spring', 'Spring Framework'),
                                         (3, 'SQL', 'Structured Query Language')
-- add more rows as needed
;

-- Insert data into the Lesson table
INSERT INTO Lesson (id, tag_id, name, content) VALUES
                                               (1, 1, 'Introduction to Java', 'This lesson covers the basics of Java programming.'),
                                               (2, 1, 'Object-Oriented Programming', 'Learn about OOP concepts in Java.'),
                                               (3, 2, 'Spring Boot Overview', 'Introduction to Spring Boot framework.'),
                                               (4, 2, 'Dependency Injection', 'Understanding the concept of dependency injection in Spring.'),
                                               (5, 3, 'SQL Basics', 'Introduction to basic SQL commands.')
;
