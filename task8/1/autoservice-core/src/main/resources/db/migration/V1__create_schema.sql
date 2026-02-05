-- Flyway migration V1: создание схемы БД автосервиса
-- База данных: H2
-- Именование: V1_create_schema.sql

-- Таблица механиков
CREATE TABLE IF NOT EXISTS mechanics (
    id INT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Таблица гаражных мест
CREATE TABLE IF NOT EXISTS garage_slots (
    id INT PRIMARY KEY,
    is_occupied BOOLEAN DEFAULT FALSE NOT NULL
);

-- Таблица заказов
CREATE TABLE IF NOT EXISTS service_orders (
    id INT PRIMARY KEY,
    mechanic_id INT NOT NULL,
    garage_slot_id INT NOT NULL,
    time_slot_start TIMESTAMP NOT NULL,
    time_slot_end TIMESTAMP NOT NULL,
    price INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'NEW',
    submitted_at TIMESTAMP,
    finished_at TIMESTAMP,
    FOREIGN KEY (mechanic_id) REFERENCES mechanics(id) ON DELETE CASCADE,
    FOREIGN KEY (garage_slot_id) REFERENCES garage_slots(id) ON DELETE CASCADE,
    CONSTRAINT check_time_slot CHECK (time_slot_start < time_slot_end),
    CONSTRAINT check_price CHECK (price >= 0),
    CONSTRAINT check_status CHECK (status IN ('NEW', 'CLOSED', 'CANCELED', 'DELETED'))
);

-- Индексы
CREATE INDEX IF NOT EXISTS idx_orders_mechanic ON service_orders(mechanic_id);
CREATE INDEX IF NOT EXISTS idx_orders_garage_slot ON service_orders(garage_slot_id);
CREATE INDEX IF NOT EXISTS idx_orders_status ON service_orders(status);
CREATE INDEX IF NOT EXISTS idx_orders_time_slot ON service_orders(time_slot_start, time_slot_end);
