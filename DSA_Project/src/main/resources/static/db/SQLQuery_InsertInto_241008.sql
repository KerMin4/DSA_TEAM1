/*
 * 2024-10-09
 * Insert query to test
 * CreateTables_10 ver.   
 */
use kkirikkiri;
select * from user;
select * from usergroup;
select * from userplace;
select * from socialgroup;
select * from place;
select * from notification;
select * from transaction;
select * from reservation;
select * from bookmark;
select * from memberhashtag;
select * from grouphashtag;

-- User Table 데이터 삽입
INSERT INTO User (user_id, username, password, name, birth, gender, phone_number, email, preferred_location, join_method, profile_image, user_type) VALUES
('user1', 'John Doe', 'password1', 'Johnny', '930605', '1', '010-1234-5678', 'john@example.com', 'Seoul', 'website', 'john_profile.jpg', 'USER'),
('user2', 'Jane Smith', 'password2', 'Janey', '290307', '2', '010-2345-6789', 'jane@example.com', 'Busan', 'kakao', 'jane_profile.jpg', 'USER'),
('vendor1', 'Vendor A', 'password3', 'VendorA', '200101', '2', '010-3456-7890', 'vendor@example.com', 'Incheon', 'website', 'vendorA_profile.jpg', 'VENDOR');

-- SocialGroup Table 데이터 삽입
INSERT INTO SocialGroup (group_name, description, profile_image, location, group_leader_id, group_join_method, member_limit, event_date) VALUES
('Photography Club', 'A club for photography enthusiasts', 'photo_club.jpg', 'Seoul', 'user1', 'auto', 50, '2024-12-01 12:00:00'),
('Cooking Club', 'Learn and share cooking skills', 'cook_club.jpg', 'Busan', 'user2', 'approval', 20, '2024-11-10 10:00:00'),
('Art Enthusiasts', 'Group for art lovers', 'art_club.jpg', 'Incheon', 'vendor1', 'auto', 30, '2024-12-15 14:00:00');

-- UserGroup Table 데이터 삽입
INSERT INTO UserGroup (user_id, group_id, status) VALUES
('user1', 1, 'approved'),
('user2', 2, 'approved'),
('vendor1', 3, 'approved');

-- Interests Table 데이터 삽입
INSERT INTO Interest (user_id, interest) VALUES
('user1', 'hobby'),
('user1', 'culture'),
('user2', 'travel');

-- Notification Table 데이터 삽입
INSERT INTO Notification (user_id, message) VALUES
('user1', 'Your group join request has been approved'),
('user2', 'New event in your favorite category'),
('vendor1', 'Your place has received a new reservation');

-- Place Table 데이터 삽입
INSERT INTO Place (title, description, profile_image, category, location, event_date, required_members, member_limit, price, vendor_id) VALUES
('Art Exhibition', 'Modern art exhibition', 'art_exhibit.jpg', 'exhibition', 'Seoul Art Museum', '2024-11-20 10:00:00', 10, 50, 15.00, 'vendor1'),
('Cooking Workshop', 'Learn to cook Italian dishes', 'cook_workshop.jpg', 'hobby', 'Busan Kitchen', '2024-12-05 14:00:00', 5, 20, 50.00, 'vendor1'),
('Jazz Concert', 'Live jazz music', 'jazz_concert.jpg', 'show', 'Incheon Jazz Hall', '2024-12-10 19:00:00', 20, 100, 75.00, 'vendor1');

-- UserPlace Table 데이터 삽입
INSERT INTO UserPlace (user_id, place_id, status) VALUES
('user1', 1, 'confirmed'),
('user2', 2, 'confirmed'),
('vendor1', 3, 'confirmed');

-- Posts Table 데이터 삽입
INSERT INTO Post (group_id, user_id) VALUES
(1, 'user1'),
(2, 'user2'),
(3, 'vendor1');

-- Photos Table 데이터 삽입
INSERT INTO Photo (post_id, image_name) VALUES
(1, 'photo1.jpg'),
(2, 'photo2.jpg'),
(3, 'photo3.jpg');

-- Reply Table 데이터 삽입
insert into Reply (post_id, user_id, content) values
(1, 'user2', 'Wow'),
(2, 'vendor1', "I'ts amazing"),
(3, 'user1', "Let's go");

-- Bookmark Table 데이터 삽입
INSERT INTO Bookmark (user_id, place_id, group_id) VALUES
('user1', NULL, 1),
('user2', NULL, 2),
('vendor1', 3, NULL);

-- MemberHashtag Table 데이터 삽입
INSERT INTO MemberHashtag (user_id, name) VALUES
('user1', '#photography'),
('user2', '#cooking'),
('vendor1', '#art');

-- GroupHashtag Table 데이터 삽입
INSERT INTO GroupHashtag (group_id, name) VALUES
(1, '#photography'),
(2, '#cooking'),
(3, '#art');

-- Transaction Table 데이터 삽입
INSERT INTO Transaction (group_id, leader_id, place_id, user_id, amount, transaction_type, status) VALUES
(1, 'user1', NULL, 'user1', 150.00, 'SETTLEMENT', 'PAID'),
(2, 'user2', NULL, 'user2', 100.00, 'SETTLEMENT', 'PENDING'),
(NULL, 'vendor1', 1, 'user1', 15.00, 'PAYMENT', 'PAID');

-- Reservation Table 데이터 삽입
INSERT INTO Reservation (user_id, place_id, event_date, price, payment_status) VALUES
('user1', 1, '2024-11-20 10:00:00', 15.00, 'COMPLETED'),
('user2', 2, '2024-12-05 14:00:00', 50.00, 'PENDING'),
('vendor1', 3, '2024-12-10 19:00:00', 75.00, 'COMPLETED');
