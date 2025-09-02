-- Testdata voor Fonds de Lecture Libre API
-- Deze data wordt automatisch geladen bij applicatie startup

-- Insert test roles (als ze nog niet bestaan)
INSERT INTO roles (name) VALUES ('ROLE_ADMIN') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (name) VALUES ('ROLE_MEMBER') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (name) VALUES ('ROLE_GUEST') ON CONFLICT (name) DO NOTHING;

-- Insert test users
-- Admin wordt automatisch aangemaakt door AdminInitializer
-- testmember / password123, testguest / password123

INSERT INTO users (username, email, password) VALUES 
('testmember', 'member@test.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a')
ON CONFLICT (username) DO NOTHING;

INSERT INTO users (username, email, password) VALUES 
('testguest', 'guest@test.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a')
ON CONFLICT (username) DO NOTHING;

-- Assign roles to users

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
SELECT 'Test', 'Member', 'Lezerslaan 10, 2000 Antwerpen', '+32 3 987 65 43', u.id
FROM users u WHERE u.username = 'testmember'
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_profiles (first_name, last_name, address, phone_number, user_id)
SELECT 'Test', 'Guest', 'Gaststraat 5, 3000 Leuven', '+32 16 555 44 33', u.id
FROM users u WHERE u.username = 'testguest'
ON CONFLICT (user_id) DO NOTHING;

-- Insert test categories
INSERT INTO categories (name, description) VALUES 
('Fictie', 'Romans en verhalen uit de verbeelding')
ON CONFLICT (name) DO NOTHING;

INSERT INTO categories (name, description) VALUES 
('Non-fictie', 'Waargebeurde verhalen en informatieve boeken')
ON CONFLICT (name) DO NOTHING;

INSERT INTO categories (name, description) VALUES 
('Klassiekers', 'Tijdloze literaire werken')
ON CONFLICT (name) DO NOTHING;

INSERT INTO categories (name, description) VALUES 
('Romantiek', 'Liefdesverhalen en romantische romans')
ON CONFLICT (name) DO NOTHING;

INSERT INTO categories (name, description) VALUES 
('Thriller', 'Spannende en mysterieuze verhalen')
ON CONFLICT (name) DO NOTHING;

-- Insert test reviews for e-books (assuming e-books with IDs 1-4 exist)
-- Reviews from testmember user
INSERT INTO reviews (rating, content, created_at, ebook_id, user_id)
SELECT 5, 'Uitstekend boek! Zeer ontroerend verhaal over familie en liefde. De karakterontwikkeling is prachtig uitgewerkt.', CURRENT_TIMESTAMP, 1, u.id
FROM users u WHERE u.username = 'testmember'
ON CONFLICT DO NOTHING;

INSERT INTO reviews (rating, content, created_at, ebook_id, user_id)
SELECT 4, 'Mooi geschreven roman met diepgaande emoties. Sommige passages zijn wat langdradig, maar over het algemeen zeer de moeite waard.', CURRENT_TIMESTAMP, 2, u.id
FROM users u WHERE u.username = 'testmember'
ON CONFLICT DO NOTHING;

INSERT INTO reviews (rating, content, created_at, ebook_id, user_id)
SELECT 5, 'Prachtige poëtische taal en een verhaal dat je raakt. De stilte spreekt boekdelen in dit meesterwerk.', CURRENT_TIMESTAMP, 3, u.id
FROM users u WHERE u.username = 'testmember'
ON CONFLICT DO NOTHING;

-- Reviews from testguest user
INSERT INTO reviews (rating, content, created_at, ebook_id, user_id)
SELECT 3, 'Interessant verhaal, maar niet helemaal mijn smaak. De schrijfstijl is goed, maar het tempo is wat traag.', CURRENT_TIMESTAMP, 2, u.id
FROM users u WHERE u.username = 'testguest'
ON CONFLICT DO NOTHING;

INSERT INTO reviews (rating, content, created_at, ebook_id, user_id)
SELECT 4, 'Mooie beschrijvingen en goede karakters. Het einde had wat sterker gekund, maar over het algemeen een aanrader.', CURRENT_TIMESTAMP, 4, u.id
FROM users u WHERE u.username = 'testguest'
ON CONFLICT DO NOTHING;

-- Additional diverse reviews to improve test coverage
INSERT INTO reviews (rating, content, created_at, ebook_id, user_id)
SELECT 2, 'Helaas viel dit boek tegen. De plot was voorspelbaar en de karakters weinig ontwikkeld.', CURRENT_TIMESTAMP, 3, u.id
FROM users u WHERE u.username = 'testguest'
ON CONFLICT DO NOTHING;
