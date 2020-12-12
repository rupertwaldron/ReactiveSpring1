CREATE TABLE IF NOT EXISTS items (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255),
    price       DECIMAL(10, 2)
);