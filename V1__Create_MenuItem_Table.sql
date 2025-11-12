CREATE TABLE MenuItem (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    category VARCHAR(50) NOT NULL
);

INSERT INTO MenuItem (name, price, category) VALUES
    ('Glazed Donut', 1.49, 'Donuts'),
    ('Chocolate Sprinkles', 1.79, 'Donuts'),
    ('Boston Creme', 1.99, 'Donuts'),
    ('House Coffee', 2.00, 'Beverages'),
    ('Latte', 3.00, 'Beverages'),
    ('Breakfast Sandwich', 4.50, 'Food');