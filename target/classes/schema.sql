
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS visitors;


CREATE TABLE visitors (
    id SERIAL PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20) NOT NULL
);


CREATE TABLE bookings (
    id SERIAL PRIMARY KEY,
    visitor_id INT NOT NULL,
    visit_date DATE NOT NULL,
    price DECIMAL(10, 2) NOT NULL CHECK (price >= 0),   
    status VARCHAR(20) NOT NULL,
    CONSTRAINT fk_visitor FOREIGN KEY (visitor_id) REFERENCES visitors(id) ON DELETE CASCADE
);


INSERT INTO visitors (full_name, email, phone) VALUES
('Иван Сергеевич Иванов', 'ivanov@mail.ru', '+79991112233'),
('Анна Игоревна Петрова', 'petrova@mail.ru', '+79992223344'),
('Сергей Владимирович Сидоров', 'sidorov@mail.ru', '+79993334455'),
('Елена Николаевна Кузнецова', 'kuznec@mail.ru', '+79994445566'),
('Михаил Александрович Попов', 'popov@mail.ru', '+79995556677');


INSERT INTO bookings (visitor_id, visit_date, price, status) VALUES
(1, '2026-06-01', 500.00, 'CONFIRMED'),
(1, '2026-06-15', 600.00, 'COMPLETED'),
(2, '2026-06-02', 500.00, 'CREATED'),
(3, '2026-06-03', 750.00, 'CANCELLED'),
(4, '2026-06-04', 500.00, 'CONFIRMED'),
(5, '2026-06-05', 1000.00, 'COMPLETED'),
(2, '2026-06-10', 400.00, 'CREATED'),
(3, '2026-06-11', 500.00, 'CONFIRMED'),
(4, '2026-06-12', 600.00, 'CREATED'),
(5, '2026-06-13', 1200.00, 'COMPLETED');
