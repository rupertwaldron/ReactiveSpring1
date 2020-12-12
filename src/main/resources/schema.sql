CREATE TABLE IF NOT EXISTS items (
    id          SERIAL PRIMARY KEY,
    description VARCHAR(255),
    price       DECIMAL(10, 2)
);