-- PRODUCT
INSERT INTO product VALUES
('A', 'A100', 'PC'),
('A', 'A200', 'PC'),
('A', 'A300', 'Laptop'),
('B', 'B100', 'PC'),
('B', 'B200', 'Laptop'),
('B', 'B300', 'Printer'),
('C', 'C100', 'Printer'),
('C', 'C200', 'PC'),
('D', 'D100', 'Laptop'),
('D', 'D200', 'Laptop');

-- PC
INSERT INTO pc VALUES
(1, 'A100', 500, 64, 10, '12x', 450),
(2, 'A200', 750, 128, 20, '24x', 650),
(3, 'B100', 450, 64, 10, '12x', 400),
(4, 'C200', 800, 256, 40, '24x', 900);

-- LAPTOP
INSERT INTO laptop VALUES
(10, 'A300', 700, 128, 100, 1200, 15),
(11, 'B200', 750, 256, 120, 1500, 14),
(12, 'D100', 600, 128, 80, 900, 13),
(13, 'D200', 800, 512, 150, 2000, 17);

-- PRINTER
INSERT INTO printer VALUES
(20, 'B300', 'y', 'Laser', 300),
(21, 'C100', 'y', 'Jet', 250);
