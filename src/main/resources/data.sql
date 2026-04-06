INSERT INTO `role` (`name`) VALUES
  ('USER'),
  ('ADMIN'),
  ('MANAGER');

INSERT INTO `category` (`name`) VALUES
('Computers'),
('Components'),
('Peripherals'),
('Storage'),
('Accessories');

INSERT INTO `product` (`name`, `description`, `sku`, `price`, `stock`, `active`) VALUES
('Dell XPS 13 Laptop', 'Compact and high-performance ultrabook', 'DEL-XPS-1F3', 1299.99, 10, true),
('NVIDIA RTX 4070 Graphics Card', 'High-end GPU for gaming and performance', 'NVI-RTX-O70', 599.99, 5, true),
('Logitech G Pro Mechanical Keyboard', 'RGB mechanical gaming keyboard', 'LOG-GPR-OFE', 129.99, 20, true),
('Samsung 1TB SSD', 'High-speed NVMe solid state drive', 'SAM-1TB-SSD', 99.99, 15, true),
('Logitech MX Master 3 Mouse', 'Ergonomic wireless mouse perfect for your wrist', 'LOG-MXM-RX3', 89.99, 25, true);

INSERT INTO `product_categories` (`product_id`, `category_id`) VALUES
(1, 1),
(2, 2),
(3, 3),
(3, 5),
(4, 4),
(4, 2),
(5, 3),
(5, 5);
