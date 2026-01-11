
-- SELECT-запросы
-- БД: Product / PC / Laptop / Printer


-- 1. Найти номер модели, скорость и размер жесткого диска
--    для всех ПК стоимостью менее 500 долларов
SELECT model, speed, hd
FROM pc
WHERE price < 500;


-- 2. Найти производителей принтеров
SELECT DISTINCT maker
FROM product
WHERE type = 'Printer';


-- 3. Найти номер модели, объем памяти и размер экрана
--    ноутбуков дороже 1000 долларов
SELECT model, ram, screen
FROM laptop
WHERE price > 1000;


-- 4. Найти все записи таблицы Printer для цветных принтеров
SELECT *
FROM printer
WHERE color = 'y';


-- 5. Найти ПК с CD 12x или 24x и ценой менее 600 долларов
SELECT model, speed, hd
FROM pc
WHERE cd IN ('12x', '24x')
  AND price < 600;


-- 6. Указать производителя и скорость ноутбуков
--    с жестким диском не менее 100 Гбайт
SELECT p.maker, l.speed
FROM laptop l
JOIN product p ON l.model = p.model
WHERE l.hd >= 100;


-- 7. Найти номера моделей и цены всех продуктов
--    производителя B
SELECT model, price
FROM pc
JOIN product USING (model)
WHERE maker = 'B'
UNION
SELECT model, price
FROM laptop
JOIN product USING (model)
WHERE maker = 'B'
UNION
SELECT model, price
FROM printer
JOIN product USING (model)
WHERE maker = 'B';


-- 8. Найти производителей, выпускающих ПК, но не ноутбуки
SELECT DISTINCT maker
FROM product
WHERE type = 'PC'
  AND maker NOT IN (
      SELECT maker
      FROM product
      WHERE type = 'Laptop'
  );


-- 9. Найти производителей ПК с процессором не менее 450 МГц
SELECT DISTINCT p.maker
FROM pc
JOIN product p ON pc.model = p.model
WHERE pc.speed >= 450;


-- 10. Найти принтеры с максимальной ценой
SELECT model, price
FROM printer
WHERE price = (SELECT MAX(price) FROM printer);


-- 11. Найти среднюю скорость ПК
SELECT AVG(speed) AS avg_speed
FROM pc;


-- 12. Найти среднюю скорость ноутбуков
--     дороже 1000 долларов
SELECT AVG(speed) AS avg_speed
FROM laptop
WHERE price > 1000;


-- 13. Найти среднюю скорость ПК производителя A
SELECT AVG(pc.speed) AS avg_speed
FROM pc
JOIN product p ON pc.model = p.model
WHERE p.maker = 'A';


-- 14. Для каждой скорости процессора ПК
--     найти среднюю цену
SELECT speed, AVG(price) AS avg_price
FROM pc
GROUP BY speed;


-- 15. Найти размеры жестких дисков,
--     встречающиеся у двух и более ПК
SELECT hd
FROM pc
GROUP BY hd
HAVING COUNT(*) >= 2;


-- 16. Найти пары моделей ПК с одинаковыми speed и RAM
--     (каждая пара выводится один раз)
SELECT p1.model AS model_big,
       p2.model AS model_small,
       p1.speed,
       p1.ram
FROM pc p1
JOIN pc p2
  ON p1.speed = p2.speed
 AND p1.ram = p2.ram
 AND p1.model > p2.model;


-- 17. Найти модели ноутбуков,
--     скорость которых меньше скорости любого ПК
SELECT 'Laptop' AS type, model, speed
FROM laptop
WHERE speed < ALL (SELECT speed FROM pc);


-- 18. Найти производителей самых дешевых цветных принтеров
SELECT DISTINCT p.maker, pr.price
FROM printer pr
JOIN product p ON pr.model = p.model
WHERE pr.color = 'y'
  AND pr.price = (
      SELECT MIN(price)
      FROM printer
      WHERE color = 'y'
  );


-- 19. Для каждого производителя найти
--     средний размер экрана ноутбуков
SELECT p.maker, AVG(l.screen) AS avg_screen
FROM laptop l
JOIN product p ON l.model = p.model
GROUP BY p.maker;


-- 20. Найти производителей, выпускающих
--     не менее трех различных моделей ПК
SELECT p.maker, COUNT(*) AS models_count
FROM pc
JOIN product p ON pc.model = p.model
GROUP BY p.maker
HAVING COUNT(*) >= 3;


-- 21. Найти максимальную цену ПК
--     для каждого производителя
SELECT p.maker, MAX(pc.price) AS max_price
FROM pc
JOIN product p ON pc.model = p.model
GROUP BY p.maker;


-- 22. Для каждой скорости ПК больше 600 МГц
--     найти среднюю цену
SELECT speed, AVG(price) AS avg_price
FROM pc
WHERE speed > 600
GROUP BY speed;


-- 23. Найти производителей, выпускающих
--     и ПК, и ноутбуки со скоростью ≥ 750 МГц
SELECT DISTINCT p.maker
FROM product p
JOIN pc ON p.model = pc.model AND pc.speed >= 750
JOIN laptop l ON p.maker = (
    SELECT maker FROM product WHERE model = l.model
)
WHERE l.speed >= 750;


-- 24. Перечислить номера моделей любых типов,
--     имеющих максимальную цену в БД
SELECT model
FROM (
    SELECT model, price FROM pc
    UNION ALL
    SELECT model, price FROM laptop
    UNION ALL
    SELECT model, price FROM printer
) t
WHERE price = (
    SELECT MAX(price)
    FROM (
        SELECT price FROM pc
        UNION ALL
        SELECT price FROM laptop
        UNION ALL
        SELECT price FROM printer
    ) x
);


-- 25. Найти производителей принтеров,
--     которые производят ПК с минимальной RAM
--     и максимальной скоростью среди таких ПК
SELECT DISTINCT p.maker
FROM product p
JOIN pc ON p.model = pc.model
WHERE pc.ram = (SELECT MIN(ram) FROM pc)
  AND pc.speed = (
      SELECT MAX(speed)
      FROM pc
      WHERE ram = (SELECT MIN(ram) FROM pc)
  );
