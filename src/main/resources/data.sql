-- Testdata voor Fonds de Lecture Libre API
-- Deze data wordt automatisch geladen bij applicatie startup

-- Insert test roles (als ze nog niet bestaan)
INSERT INTO roles (name) VALUES ('ROLE_ADMIN') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (name) VALUES ('ROLE_MEMBER') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (name) VALUES ('ROLE_GUEST') ON CONFLICT (name) DO NOTHING;

-- Insert test users (wachtwoorden zijn BCrypt encoded)
-- Admin: Admin / fondsdelecturelibre
-- Member: testmember / password123  
-- Guest: testguest / password123
INSERT INTO users (username, email, password) VALUES 
('Admin', 'admin@fondsdelecturelibre.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a')
ON CONFLICT (username) DO NOTHING;

INSERT INTO users (username, email, password) VALUES 
('testmember', 'member@test.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a')
ON CONFLICT (username) DO NOTHING;

INSERT INTO users (username, email, password) VALUES 
('testguest', 'guest@test.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a')
ON CONFLICT (username) DO NOTHING;

-- Assign roles to users
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id FROM users u, roles r 
WHERE u.username = 'Admin' AND r.name = 'ROLE_ADMIN'
ON CONFLICT DO NOTHING;

INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id FROM users u, roles r 
WHERE u.username = 'testmember' AND r.name = 'ROLE_MEMBER'
ON CONFLICT DO NOTHING;

INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id FROM users u, roles r 
WHERE u.username = 'testguest' AND r.name = 'ROLE_GUEST'
ON CONFLICT DO NOTHING;

-- Insert test user profiles
INSERT INTO user_profiles (first_name, last_name, address, phone_number, user_id)
SELECT 'Admin', 'Beheerder', 'Bibliotheekstraat 1, 1000 Brussel', '+32 2 123 45 67', u.id
FROM users u WHERE u.username = 'Admin'
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_profiles (first_name, last_name, address, phone_number, user_id)
SELECT 'Test', 'Member', 'Lezerslaan 10, 2000 Antwerpen', '+32 3 987 65 43', u.id
FROM users u WHERE u.username = 'testmember'
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_profiles (first_name, last_name, address, phone_number, user_id)
SELECT 'Test', 'Guest', 'Gaststraat 5, 3000 Leuven', '+32 16 555 44 33', u.id
FROM users u WHERE u.username = 'testguest'
ON CONFLICT (user_id) DO NOTHING;

-- Note: EBooks worden automatisch geladen door EBookDataLoader
-- Reviews kunnen toegevoegd worden na het uploaden van boeken
