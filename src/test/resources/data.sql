-- Rollen toevoegen
INSERT INTO roles (id, name) VALUES (1, 'ROLE_MEMBER');
INSERT INTO roles (id, name) VALUES (2, 'ROLE_ADMIN');

-- Testgebruikers toevoegen (wachtwoorden zijn allemaal 'password123')
INSERT INTO users (id, username, email, password, enabled) VALUES 
(1, 'member', 'member@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFVMLVZqpjxSOpSy83LKbiq', true),
(2, 'admin', 'admin@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFVMLVZqpjxSOpSy83LKbiq', true),
(3, 'test', 'test@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFVMLVZqpjxSOpSy83LKbiq', true);

-- Rollen toekennen aan gebruikers
INSERT INTO user_roles (user_id, role_id) VALUES 
(1, 1),  -- member heeft ROLE_MEMBER
(2, 2),  -- admin heeft ROLE_ADMIN
(3, 1),  -- test heeft ROLE_MEMBER
(3, 2);  -- test heeft ook ROLE_ADMIN
