
-- Таблица PRODUCT

CREATE TABLE product (
    maker VARCHAR(10),
    model VARCHAR(50) PRIMARY KEY,
    type  VARCHAR(50)
);


-- Таблица PC

CREATE TABLE pc (
    code  INT PRIMARY KEY,
    model VARCHAR(50),
    speed SMALLINT,
    ram   SMALLINT,
    hd    REAL,
    cd    VARCHAR(10),
    price MONEY,
    CONSTRAINT fk_pc_product
        FOREIGN KEY (model) REFERENCES product(model)
);


-- Таблица LAPTOP

CREATE TABLE laptop (
    code   INT PRIMARY KEY,
    model  VARCHAR(50),
    speed  SMALLINT,
    ram    SMALLINT,
    hd     REAL,
    price  MONEY,
    screen SMALLINT,
    CONSTRAINT fk_laptop_product
        FOREIGN KEY (model) REFERENCES product(model)
);

-- Таблица PRINTER

CREATE TABLE printer (
    code  INT PRIMARY KEY,
    model VARCHAR(50),
    color CHAR(1),
    type  VARCHAR(10),
    price MONEY,
    CONSTRAINT fk_printer_product
        FOREIGN KEY (model) REFERENCES product(model)
);
