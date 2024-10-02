/*
 * 2024-10-02
 * Insert query
 * To test   
 */
use kkirikkiri;
-- select * from user;
-- select * from usergroup;
-- select * from userplace;
-- select * from socialgroup;
-- select * from place;
-- select * from notification;
-- select * from transaction;
-- select * from reservation;
-- select * from bookmark;
-- select * from memberhashtag;
-- select * from grouphashtag;

-- 제약 조건 삭제
ALTER TABLE Bookmark DROP CONSTRAINT CK_NOTNULL_place_id_OR_group_id;

-- 수정된 제약 조건 추가
ALTER TABLE Bookmark ADD CONSTRAINT CK_NOTNULL_place_id_OR_group_id CHECK (
    (place_id IS NOT NULL AND group_id IS NULL) OR 
    (place_id IS NULL AND group_id IS NOT NULL)
);

-- User Table
INSERT INTO User (username, password, name, phone_number, email, interests, preferred_location, join_method, profile_image, user_type) VALUES
	('user1', 'password1', 'User One', '010-1234-5678', 'user1@example.com', 'sports, music', 'Seoul', 'website', 'profile1.jpg', 'user'),
	('user2', 'password2', 'User Two', '010-2345-6789', 'user2@example.com', 'travel, cooking', 'Busan', 'kakao', 'profile2.jpg', 'user'),
	('vendor1', 'vendorpassword', 'Vendor One', '010-3456-7890', 'vendor1@example.com', 'art, culture', 'Incheon', 'website', 'vendor_profile1.jpg', 'vendor');

-- SocialGroup Table
INSERT INTO SocialGroup (group_name, description, profile_image, interest, location, group_leader_id, group_join_method, member_limit, event_date) VALUES
	('Sports Lovers', 'A group for sports enthusiasts.', 'group1.jpg', 'sports', 'Seoul', 1, 'auto', 20, '2024-11-01 10:00:00'),
	('Foodies Unite', 'For those who love to explore food.', 'group2.jpg', 'food', 'Busan', 2, 'approval', 15, '2024-11-02 12:00:00'),
	('Travel Buddies', 'Travel enthusiasts sharing experiences.', 'group3.jpg', 'travel', 'Incheon', 3, 'auto', 10, '2024-11-03 14:00:00');

-- UserGroup Table
INSERT INTO UserGroup (user_id, group_id, joined_at, status) VALUES
	(1, 1, CURRENT_TIMESTAMP, 'approved'),
	(2, 2, CURRENT_TIMESTAMP, 'pending'),
	(1, 3, CURRENT_TIMESTAMP, 'approved');

-- Notification Table
INSERT INTO Notification (user_id, message, read_status) VALUES
	(1, 'Welcome to Sports Lovers!', FALSE),
	(2, 'Your request to join Foodies Unite is pending.', FALSE),
	(3, 'New event added to Travel Buddies.', TRUE);

-- Place Table
INSERT INTO Place (title, description, location, event_date, required_members, current_members, price, vendor_id) VALUES
	('Football Match', 'Join us for an exciting football match!', 'Seoul', '2024-11-01 15:00:00', 10, 0, 20.00, 3),
	('Cooking Class', 'Learn to cook delicious dishes.', 'Busan', '2024-11-02 13:00:00', 5, 0, 50.00, 3),
	('Art Exhibition', 'Explore beautiful artworks.', 'Incheon', '2024-11-03 11:00:00', 20, 0, 10.00, 3);

-- UserPlace Table
INSERT INTO UserPlace (user_id, place_id, joined_at, status) VALUES
	(1, 1, CURRENT_TIMESTAMP, 'confirmed'),
	(2, 2, CURRENT_TIMESTAMP, 'pending'),
	(3, 3, CURRENT_TIMESTAMP, 'confirmed');

-- Bookmark Table
INSERT INTO Bookmark (user_id, place_id, group_id, created_at) VALUES
	(1, 1, NULL, CURRENT_TIMESTAMP),
	(2, NULL, 2, CURRENT_TIMESTAMP),
	(3, 3, NULL, CURRENT_TIMESTAMP);

-- MemberHashtag Table
INSERT INTO MemberHashtag (user_id, name) VALUES
	(1, 'sports'),
	(2, 'food'),
	(3, 'travel');

-- GroupHashtag Table
INSERT INTO GroupHashtag (group_id, name) VALUES
	(1, 'sports'),
	(2, 'food'),
	(3, 'travel');

-- Transaction Table
INSERT INTO Transaction (group_id, leader_id, place_id, user_id, amount, transaction_type, status) VALUES
	(1, 1, 1, 1, 200.00, 'PAYMENT', 'PENDING'),
	(2, 2, 2, 2, 150.00, 'SETTLEMENT', 'PAID'),
	(3, 3, 3, 3, 300.00, 'PAYMENT', 'CANCELLED');


-- Reservation Table
INSERT INTO Reservation (user_id, place_id, reservation_date, status, price, event_date) VALUES
	(1, 1, CURRENT_TIMESTAMP, 'PENDING', 20.00, '2024-11-01 15:00:00'),
	(2, 2, CURRENT_TIMESTAMP, 'CONFIRMED', 50.00, '2024-11-02 13:00:00'),
	(3, 3, CURRENT_TIMESTAMP, 'CANCELED', 10.00, '2024-11-03 11:00:00');
