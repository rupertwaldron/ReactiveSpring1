DELETE from items;
ALTER TABLE items AUTO_INCREMENT = 1;
INSERT INTO items (description, price)
values  ('Samsung TV', 399.99),
        ('Apple Watch', 299.99),
        ('BMW M3', 540000.00),
        ('Apple Headphones', 149.99);