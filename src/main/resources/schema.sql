-- Создание таблицы Посетителей
CREATE TABLE visitors (
    id SERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20) NOT NULL
);

-- Создание таблицы Бронирований
CREATE TABLE bookings (
    id SERIAL PRIMARY KEY,
    visitor_id INT NOT NULL,
    visit_date TIMESTAMP NOT NULL,
    ticket_count INT NOT NULL CHECK (ticket_count > 0),
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_visitor FOREIGN KEY (visitor_id) REFERENCES visitors(id) ON DELETE CASCADE
);

-- Начальные данные (5 посетителей)
INSERT INTO visitors (full_name, email, phone) VALUES 
('Иван Иванов', 'ivan@example.com', '+79001112233'),
('Петр Петров', 'petr@example.com', '+79004445566'),
('Анна Сидорова', 'anna@example.com', '+79007778899'),
('Мария Кузнецова', 'maria@example.com', '+79001231231'),
('Алексей Смирнов', 'alex@example.com', '+79004564564');

-- Начальные данные (10 бронирований, разные статусы)
INSERT INTO bookings (visitor_id, visit_date, ticket_count, status) VALUES 
(1, '2026-06-01 10:00:00', 2, 'CREATED'),
(1, '2026-06-02 12:00:00', 1, 'CONFIRMED'),
(2, '2026-06-01 11:30:00', 4, 'COMPLETED'),
(3, '2026-06-03 14:00:00', 2, 'CANCELLED'),
(4, '2026-06-04 15:00:00', 3, 'CREATED'),
(5, '2026-06-05 09:00:00', 1, 'CONFIRMED'),
(2, '2026-06-06 16:00:00', 2, 'CREATED'),
(3, '2026-06-07 10:30:00', 5, 'CONFIRMED'),
(4, '2026-06-08 12:00:00', 1, 'COMPLETED'),
(5, '2026-06-09 13:00:00', 2, 'CANCELLED');