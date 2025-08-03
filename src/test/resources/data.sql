INSERT INTO roles (id, name) VALUES (1, 'ROLE_MEMBER');
INSERT INTO roles (id, name) VALUES (2, 'ROLE_ADMIN');

INSERT INTO users (id, username, email, password) VALUES 
(1, 'testuser', 'test@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFVMLVZqpjxSOpSy83LKbiq');

INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);
