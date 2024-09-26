/*
 * 2024-09-26
 * MySQL, DBeaver
 * Tables: User, SocialGroup, UserGroup, Notification, Trend, Place, UserPlace, MemberHashtag, GroupHashtag, Transaction, Reservation
 */

create database kkirikkiri_0;
select database();
use kkirikkiri_0;
show tables from kkirikkiri_0;
-- drop database kkirikkiri_0;

-- User Table
CREATE TABLE User (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    email VARCHAR(255),
    interests TEXT,
    preferred_location VARCHAR(255),
    registration_type ENUM('website', 'kakao') NOT NULL,
    profile_image VARCHAR(255),
    user_type ENUM('user', 'vendor') not null,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Group Table
CREATE TABLE SocialGroup (
    group_id INT AUTO_INCREMENT PRIMARY KEY,
    group_name VARCHAR(255) NOT NULL,
    description TEXT,
    profile_image VARCHAR(255),
    interest TEXT,
    location VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    group_leader_id INT,
    join_method ENUM('auto', 'approval') NOT NULL,
    member_limit INT,
    event_date DATETIME,
    FOREIGN KEY (group_leader_id) REFERENCES User(user_id)
);

-- UserGroup Table (사용자와 그룹 간의 관계를 나타내는 테이블)
CREATE TABLE UserGroup (
    user_group_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    group_id INT,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('pending', 'approved', 'rejected') DEFAULT 'approved',
    FOREIGN KEY (user_id) REFERENCES User(user_id),
    FOREIGN KEY (group_id) REFERENCES SocialGroup(group_id)
);

-- Notification Table
CREATE TABLE Notification (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    message TEXT,
    read_status BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES User(user_id)
);

-- Trend Table
CREATE TABLE Trend (
    trend_id INT AUTO_INCREMENT PRIMARY KEY,
    group_id INT,
    view_count INT DEFAULT 0,
    bookmark_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (group_id) REFERENCES SocialGroup(group_id)
);

-- Place Table
CREATE TABLE Place (
    place_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    description TEXT,
    location VARCHAR(255),
    event_date DATETIME,
    required_members INT,
    current_members INT DEFAULT 0,
    price DECIMAL(10, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    vendor_id INT,  -- registered company ID
    FOREIGN KEY (vendor_id) REFERENCES User(user_id)  -- User 테이블의 vendor_id
);

-- UserPlace Table (유저와 플레이스 간의 관계를 나타내는 테이블)
CREATE TABLE UserPlace (
    user_place_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    place_id INT,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('pending', 'confirmed', 'canceled') DEFAULT 'confirmed',
    FOREIGN KEY (user_id) REFERENCES User(user_id),
    FOREIGN KEY (place_id) REFERENCES Place(place_id)
);

-- Hashtag Table
CREATE TABLE MemberHashtag (
    hashtag_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    name VARCHAR(50) NOT null,
    foreign key (user_id) references User(user_id)
);

-- GroupHashtag Table
CREATE TABLE GroupHashtag (
	hashtag_id INT AUTO_INCREMENT PRIMARY key,
    group_id INT,
    name VARCHAR(50) not null,
    FOREIGN KEY (group_id) REFERENCES SocialGroup(group_id)
);

-- Transaction Table(payment, settlement)
CREATE TABLE Transaction (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    group_id INT,                -- 그룹과 관련된 경우
    user_id INT,                 -- 결제하는 사용자 ID
    leader_id INT,               -- 그룹 리더 (해당 그룹 결제 시)
    place_id INT,                -- 플레이스와 관련된 경우
    amount DECIMAL(10, 2) NOT NULL,
    transaction_type ENUM('PAYMENT', 'SETTLEMENT') NOT NULL,
    status ENUM('PENDING', 'PAID', 'FAILED', 'CANCELLED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    FOREIGN KEY (group_id) REFERENCES SocialGroup(group_id),
    FOREIGN KEY (user_id) REFERENCES User(user_id),
    FOREIGN KEY (leader_id) REFERENCES User(user_id),
    FOREIGN KEY (place_id) REFERENCES Place(place_id)
);

-- Reservation Table(Place)
CREATE TABLE Reservation (
    reservation_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    place_id INT,
    reservation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('PENDING', 'CONFIRMED', 'CANCELED') DEFAULT 'PENDING',
    number_of_people INT,
    price DECIMAL(10, 2),  -- 총 결제 금액
    event_date DATETIME,          -- 플레이스의 이벤트 날짜
    payment_status ENUM('PENDING', 'COMPLETED', 'FAILED') DEFAULT 'PENDING',  -- 결제 상태
    FOREIGN KEY (user_id) REFERENCES User(user_id),
    FOREIGN KEY (place_id) REFERENCES Place(place_id)
);