-- Xóa data cũ
DELETE FROM order_details;
DELETE FROM orders;
DELETE FROM products;
DELETE FROM categories;
DELETE FROM tables;
DELETE FROM users;

-- Reset auto increment
ALTER TABLE order_details AUTO_INCREMENT = 1;
ALTER TABLE orders AUTO_INCREMENT = 1;
ALTER TABLE products AUTO_INCREMENT = 1;
ALTER TABLE categories AUTO_INCREMENT = 1;
ALTER TABLE tables AUTO_INCREMENT = 1;
ALTER TABLE users AUTO_INCREMENT = 1;

-- Insert users mẫu
INSERT INTO users (id, username, password, email, full_name, phone, role, is_active, created_at, updated_at)
VALUES
(1, 'staff', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVwUi.', 'staff@coffee.com', 'Nhân Viên Phục Vụ', '0123456789', 'EMPLOYEE', true, NOW(), NOW()),
(2, 'barista', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVwUi.', 'barista@coffee.com', 'Nhân Viên Pha Chế', '0123456790', 'EMPLOYEE', true, NOW(), NOW()),
(3, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVwUi.', 'admin@coffee.com', 'Quản Lý Hệ Thống', '0123456791', 'ADMIN', true, NOW(), NOW());

-- Insert bàn mẫu
INSERT INTO tables (id, name, description, capacity, status, created_at, updated_at) 
VALUES 
(1, 'Bàn 1', 'Bàn tiêu chuẩn gần cửa sổ', 4, 'FREE', NOW(), NOW()),
(2, 'Bàn 2', 'Bàn tiêu chuẩn', 4, 'FREE', NOW(), NOW()),
(3, 'Bàn 3', 'Bàn tiêu chuẩn', 4, 'FREE', NOW(), NOW()),
(4, 'Bàn 4', 'Bàn lớn cho nhóm', 6, 'FREE', NOW(), NOW()),
(5, 'Bàn VIP 1', 'Bàn VIP view đẹp', 4, 'FREE', NOW(), NOW()),
(6, 'Bàn VIP 2', 'Bàn VIP riêng tư', 4, 'FREE', NOW(), NOW());

-- Insert categories
INSERT INTO categories (id, title, description, photo, created_at, updated_at) 
VALUES 
(1, 'Cà Phê', 'Các loại cà phê truyền thống', '/images/coffee-category.jpg', NOW(), NOW()),
(2, 'Trà', 'Trà các loại', '/images/tea-category.jpg', NOW(), NOW()),
(3, 'Nước Ép & Sinh Tố', 'Nước ép và sinh tố trái cây', '/images/juice-category.jpg', NOW(), NOW()),
(4, 'Bánh & Snack', 'Bánh ngọt và snack', '/images/cake-category.jpg', NOW(), NOW()),
(5, 'Đồ Ăn Nhẹ', 'Đồ ăn nhẹ', '/images/snack-category.jpg', NOW(), NOW());

-- Insert products - Cà Phê
INSERT INTO products (id, title, description, price, stock_quantity, photo, category_id, is_active, created_at, updated_at)
VALUES
(1, 'Cà Phê Đen Đá', 'Cà phê đen truyền thống', 25000, 100, '/images/black-coffee.jpg', 1, true, NOW(), NOW()),
(2, 'Cà Phê Đen Nóng', 'Cà phê đen nóng thơm', 25000, 100, '/images/hot-black-coffee.jpg', 1, true, NOW(), NOW()),
(3, 'Cà Phê Sữa Đá', 'Cà phê sữa đá Việt Nam', 30000, 100, '/images/milk-coffee.jpg', 1, true, NOW(), NOW()),
(4, 'Cà Phê Sữa Nóng', 'Cà phê sữa nóng', 30000, 100, '/images/hot-milk-coffee.jpg', 1, true, NOW(), NOW()),
(5, 'Bạc Xỉu', 'Cà phê sữa đá kiểu Sài Gòn', 35000, 80, '/images/bac-xiu.jpg', 1, true, NOW(), NOW()),
(6, 'Americano', 'Cà phê Americano', 40000, 80, '/images/americano.jpg', 1, true, NOW(), NOW()),
(7, 'Latte', 'Cà phê Latte', 45000, 80, '/images/latte.jpg', 1, true, NOW(), NOW()),
(8, 'Cappuccino', 'Cà phê Cappuccino', 45000, 80, '/images/cappuccino.jpg', 1, true, NOW(), NOW()),
(9, 'Espresso', 'Cà phê Espresso', 35000, 80, '/images/espresso.jpg', 1, true, NOW(), NOW()),

-- Trà
(10, 'Trà Đào Cam Sả', 'Trà đào cam sả thơm ngon', 40000, 50, '/images/peach-tea.jpg', 2, true, NOW(), NOW()),
(11, 'Trà Vải Hạt Sen', 'Trà vải hạt sen mát lạnh', 40000, 50, '/images/lychee-tea.jpg', 2, true, NOW(), NOW()),
(12, 'Trà Chanh', 'Trà chanh tươi mát', 30000, 60, '/images/lemon-tea.jpg', 2, true, NOW(), NOW()),
(13, 'Trà Sữa', 'Trà sữa thái truyền thống', 35000, 70, '/images/milk-tea.jpg', 2, true, NOW(), NOW()),
(14, 'Trà Sữa Trân Châu', 'Trà sữa trân châu đường đen', 45000, 60, '/images/bubble-tea.jpg', 2, true, NOW(), NOW()),
(15, 'Trà Gừng Mật Ong', 'Trà gừng mật ong ấm bụng', 35000, 40, '/images/ginger-tea.jpg', 2, true, NOW(), NOW()),

-- Nước Ép & Sinh Tố
(16, 'Sinh Tố Bơ', 'Sinh tố bơ dẻo thơm', 45000, 30, '/images/avocado-smoothie.jpg', 3, true, NOW(), NOW()),
(17, 'Sinh Tố Xoài', 'Sinh tố xoài chua ngọt', 40000, 30, '/images/mango-smoothie.jpg', 3, true, NOW(), NOW()),
(18, 'Sinh Tố Dâu', 'Sinh tố dâu tây tươi', 45000, 30, '/images/strawberry-smoothie.jpg', 3, true, NOW(), NOW()),
(19, 'Nước Cam Ép', 'Nước cam ép tươi', 35000, 40, '/images/orange-juice.jpg', 3, true, NOW(), NOW()),
(20, 'Nước Chanh', 'Nước chanh tươi mát', 25000, 50, '/images/lemonade.jpg', 3, true, NOW(), NOW()),
(21, 'Nước Ép Dứa', 'Nước ép dứa thơm ngon', 30000, 35, '/images/pineapple-juice.jpg', 3, true, NOW(), NOW()),

-- Bánh & Snack
(22, 'Bánh Tiramisu', 'Bánh tiramisu Ý', 45000, 20, '/images/tiramisu.jpg', 4, true, NOW(), NOW()),
(23, 'Bánh Chocolate', 'Bánh chocolate ngọt ngào', 40000, 25, '/images/chocolate-cake.jpg', 4, true, NOW(), NOW()),
(24, 'Bánh Cheesecake', 'Bánh cheesecake phô mai', 50000, 15, '/images/cheesecake.jpg', 4, true, NOW(), NOW()),
(25, 'Bánh Mousse', 'Bánh mousse dâu', 40000, 20, '/images/mousse-cake.jpg', 4, true, NOW(), NOW()),
(26, 'Cookie Socola', 'Bánh cookie socola chip', 20000, 30, '/images/cookie.jpg', 4, true, NOW(), NOW()),

-- Đồ Ăn Nhẹ
(27, 'Bánh Mì Pate', 'Bánh mì pate chả thịt', 20000, 50, '/images/banh-mi.jpg', 5, true, NOW(), NOW()),
(28, 'Bánh Mì Xíu Mại', 'Bánh mì xíu mại Sài Gòn', 25000, 40, '/images/xiu-mai.jpg', 5, true, NOW(), NOW()),
(29, 'Khoai Tây Chiên', 'Khoai tây chiên giòn', 30000, 35, '/images/french-fries.jpg', 5, true, NOW(), NOW()),
(30, 'Bánh Ngọt Pháp', 'Bánh ngọt phong cách Pháp', 25000, 30, '/images/french-cake.jpg', 5, true, NOW(), NOW());