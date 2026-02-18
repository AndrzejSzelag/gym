-- 1. ADDRESSES
INSERT INTO addresses (street, street_number, home_number, post_code, city, created_at, created_by, updated_at, last_modified_by)
VALUES
    ('Makowa', '7', '2', '00-001', 'Konin', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
    ('Lipowa', '15B', '12', '00-002', 'Kraków', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
    ('Sosnowa', '8C', '3', '00-003', 'Gdańsk', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
    ('Wierzbowa', '10', '5', '00-004', 'Poznań', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
    ('Wierzbowa', '13A', '111', '00-004', 'Poznań', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
    ('Dębowa', '6', '8', '00-006', 'Wrocław', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
    ('Klonowa', '12', '9', '00-007', 'Łódź', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
    ('Grabowa', '4', '2', '00-008', 'Szczecin', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
    ('Jesionowa', '9B', '7', '00-009', 'Lublin', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
    ('Olchowa', '3', '6', '00-010', 'Bydgoszcz', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
    ('Sosnowa', '16', '28', '00-003', 'Gdańsk', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
    ('Kasztanowa', '1', '14', '00-007', 'Łódź', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
    ('Tulipanowa', '3A', '22', '00-008', 'Szczecin', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
    ('Grzybowa', '1B', '151', '00-009', 'Lublin', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
    ('Czarny Bez', '10', '10', '00-010', 'Bydgoszcz', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
    ('Brzozowa', '2A', '1', '00-005', 'Warszawa', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system');;

-- 2. ROLES (Poprawione o brakujące kolumny auditingu)
INSERT INTO public.roles (name, created_at, created_by, updated_at, last_modified_by)
SELECT 'ROLE_ADMINISTRATOR', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'
WHERE NOT EXISTS (SELECT 1 FROM public.roles WHERE name = 'ROLE_ADMINISTRATOR');;

-- 3. CLIENTS
INSERT INTO clients (first_name, last_name, email, phone, registration_date, expiration_date, address_id, created_at, created_by, updated_at, last_modified_by)
VALUES
    ('Andrzej', 'Szeląg', 'andrzej.szelag@gym.pl', '123456789', CURRENT_DATE, CURRENT_DATE + INTERVAL '30 days', 1, CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
    ('Damian', 'Szeląg', 'damian.szelag@gym.pl', '723456777', CURRENT_DATE, CURRENT_DATE + INTERVAL '0 days', 5, CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
    ('Maria', 'Kowalska', 'maria.kowalska@gym.pl', '987654321', CURRENT_DATE, CURRENT_DATE - INTERVAL '2 days', 2, CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
    ('Piotr', 'Nowak', 'piotr.nowak@gym.pl', '555666777', CURRENT_DATE, CURRENT_DATE - INTERVAL '14 days', 3, CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
    ('Anna', 'Wiśniewska', 'anna.wisniewska@gym.pl', '111222333', CURRENT_DATE, CURRENT_DATE + INTERVAL '29 days', 4, CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
    ('Tomasz', 'Lewandowski', 'tomasz.lewandowski@gym.pl', '444555666', CURRENT_DATE, CURRENT_DATE + INTERVAL '1 days', 16, CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
    ('Katarzyna', 'Zielińska', 'katarzyna.zielinska@gym.pl', '777888999', CURRENT_DATE, CURRENT_DATE - INTERVAL '21 days', 6, CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
    ('Michał', 'Kamiński', 'michal.kaminski@gym.pl', '222333444', CURRENT_DATE, CURRENT_DATE - INTERVAL '11 days', 7, CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
    ('Joanna', 'Jankowska', 'joanna.jankowska@gym.pl', '888999000', CURRENT_DATE, CURRENT_DATE + INTERVAL '15 days', 8, CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
    ('Paweł', 'Wójcik', 'pawel.wojcik@gym.pl', '333444555', CURRENT_DATE, CURRENT_DATE + INTERVAL '8 days', 9, CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
    ('Elżbieta', 'Kowalczykiewicz', 'elzbieta.kowalczykiewicz@gym.pl', '666777888', CURRENT_DATE, CURRENT_DATE + INTERVAL '14 days', 10, CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
    ('Nikodem', 'Kozieł', 'nikodem.kozieł@gym.pl', '722789101', CURRENT_DATE, CURRENT_DATE + INTERVAL '25 days', 11, CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
    ('Emilia', 'Stanisławska-Jaworska', 'emilia.stanislawska-jaworska@gym.pl', '765190166', CURRENT_DATE, CURRENT_DATE + INTERVAL '15 days', 12, CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
    ('Roman', 'Kos', 'roman.kos@gym.pl', '766412987', CURRENT_DATE, NULL, 13, CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
    ('Tomasz', 'Iksiński', 'tomasz.iksinski@gym.pl', '777999111', CURRENT_DATE, CURRENT_DATE + INTERVAL '7 days', 14, CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
    ('Stanley', 'Sullivan', 'stan.sullivan@gym.pl', '610001917', CURRENT_DATE, NULL, 15, CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system')
ON CONFLICT (email) DO NOTHING;;

-- 4. USERS
INSERT INTO users (email, first_name, last_name, password, role_id, created_at, created_by, updated_at, last_modified_by)
SELECT
    'testuser@gym.pl',
    'test',
    'test',
    '$2a$10$tpvvsDNJCDKS8FoWFAJ8y.Y6ZeJ3V1Icd0S35RxVniVBkFfhFRFsy',
    id,
    CURRENT_TIMESTAMP,
    'system',
    CURRENT_TIMESTAMP,
    'system'
FROM roles
WHERE name = 'ROLE_ADMINISTRATOR'
  AND NOT EXISTS (SELECT 1 FROM users WHERE email = 'testuser@gym.pl')
LIMIT 1;;