
-- Инициализация структуры (удаляем старые таблицы, если они были, для чистой накатки)
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS visitors CASCADE;

-- 1. Создание таблицы посетителей
CREATE TABLE visitors (
    id SERIAL PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL, -- Изменено под формат ФИО из ConsoleMenu
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20)
);

-- 2. Создание таблицы бронирований
CREATE TABLE bookings (
    id SERIAL PRIMARY KEY,
    visitor_id INT NOT NULL,
    visit_date TIMESTAMP NOT NULL,
    ticket_count INT NOT NULL DEFAULT 1,
    status VARCHAR(20) NOT NULL DEFAULT 'CREATED',
    price NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    CONSTRAINT fk_visitor FOREIGN KEY (visitor_id) REFERENCES visitors(id) ON DELETE CASCADE
);

-- ====================================================================
-- ЗАПОЛНЕНИЕ ДЕМОНСТРАЦИОННЫМИ ДАННЫМИ (Тестовый фикстурный набор)
-- ====================================================================

-- Наполнение таблицы посетителей
INSERT INTO visitors (id, full_name, email, phone) VALUES
(1, 'Иванов Иван Иванович', 'ivanov@email.com', '+79111112233'),
(2, 'Петров Петр Петрович', 'petrov@email.com', '+79222223344'),
(3, 'Сидорова Анна Владимировна', 'sidorova@email.com', '+79333334455'),
(4, 'Кузнецов Алексей Сергеевич', 'kuznetsov@email.com', '+79444445566'),
(5, 'Смирнова Елена Дмитриевна', 'smirnova@email.com', '+79555556677');

-- Сброс счетчика инкремента ID для таблицы посетителей
SELECT setval('visitors_id_seq', (SELECT MAX(id) FROM visitors));

-- Наполнение таблицы бронирований (соответствует вашему логу из консоли)
INSERT INTO bookings (id, visitor_id, visit_date, ticket_count, status, price) VALUES
(1, 1, '2026-06-01 10:00:00', 1, 'CREATED', 250.00),
(2, 1, '2026-06-02 12:00:00', 1, 'CONFIRMED', 250.00),
(3, 2, '2026-06-01 11:30:00', 1, 'COMPLETED', 250.00),
(4, 3, '2026-06-03 14:00:00', 1, 'CANCELLED', 0.00),
(5, 4, '2026-06-04 15:00:00', 1, 'CREATED', 250.00),
(6, 5, '2026-06-05 09:00:00', 1, 'CONFIRMED', 250.00),
(7, 2, '2026-06-06 16:00:00', 1, 'CREATED', 250.00),
(8, 3, '2026-06-07 10:30:00', 1, 'CONFIRMED', 250.00),
(9, 4, '2026-06-08 12:00:00', 1, 'COMPLETED', 250.00),
(10, 5, '2026-06-09 13:00:00', 1, 'CANCELLED', 0.00),
(11, 1, '2026-09-22 15:10:00', 1, 'CREATED', 346.00);

-- Сброс счетчика инкремента ID для таблицы бронирований
SELECT setval('bookings_id_seq', (SELECT MAX(id) FROM bookings));
