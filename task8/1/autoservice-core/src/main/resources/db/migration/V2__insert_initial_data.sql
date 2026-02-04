-- Flyway migration V2: начальные тестовые данные
-- База данных: H2
-- Именование: V2_initial_data.sql

-- Очистка таблиц (в обратном порядке из-за внешних ключей)
DELETE FROM service_orders;
DELETE FROM garage_slots;
DELETE FROM mechanics;

-- Вставка тестовых механиков
INSERT INTO mechanics (id, name) VALUES
    (1, 'Андрей'),
    (2, 'Руслан'),
    (3, 'Магомед'),
    (4, 'Иван'),
    (5, 'Сергей');

-- Вставка тестовых гаражных мест
INSERT INTO garage_slots (id, is_occupied) VALUES
    (101, FALSE),
    (102, FALSE),
    (103, FALSE),
    (104, FALSE),
    (105, FALSE);

-- Вставка тестовых заказов
INSERT INTO service_orders (id, mechanic_id, garage_slot_id, time_slot_start, time_slot_end, price, status, submitted_at)
VALUES (1, 1, 101, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '2' HOUR, 5000, 'NEW', CURRENT_TIMESTAMP);

INSERT INTO service_orders (id, mechanic_id, garage_slot_id, time_slot_start, time_slot_end, price, status, submitted_at)
VALUES (2, 2, 102, CURRENT_TIMESTAMP + INTERVAL '1' DAY, CURRENT_TIMESTAMP + INTERVAL '1' DAY + INTERVAL '3' HOUR, 7500, 'NEW', CURRENT_TIMESTAMP);

INSERT INTO service_orders (id, mechanic_id, garage_slot_id, time_slot_start, time_slot_end, price, status, submitted_at, finished_at)
VALUES (3, 3, 103, CURRENT_TIMESTAMP - INTERVAL '2' DAY, CURRENT_TIMESTAMP - INTERVAL '2' DAY + INTERVAL '1' HOUR, 3000, 'CLOSED', CURRENT_TIMESTAMP - INTERVAL '2' DAY, CURRENT_TIMESTAMP - INTERVAL '2' DAY + INTERVAL '1' HOUR);

INSERT INTO service_orders (id, mechanic_id, garage_slot_id, time_slot_start, time_slot_end, price, status, submitted_at)
VALUES (4, 1, 104, CURRENT_TIMESTAMP - INTERVAL '1' DAY, CURRENT_TIMESTAMP - INTERVAL '1' DAY + INTERVAL '2' HOUR, 4000, 'CANCELED', CURRENT_TIMESTAMP - INTERVAL '1' DAY);

INSERT INTO service_orders (id, mechanic_id, garage_slot_id, time_slot_start, time_slot_end, price, status, submitted_at)
VALUES (5, 2, 105, CURRENT_TIMESTAMP - INTERVAL '3' DAY, CURRENT_TIMESTAMP - INTERVAL '3' DAY + INTERVAL '1' HOUR, 2500, 'DELETED', CURRENT_TIMESTAMP - INTERVAL '3' DAY);
