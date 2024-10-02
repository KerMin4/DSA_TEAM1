/*
 * 2024-10-02
 * CreateTables_7
 * MySQL, DBeaver
 * Upadate Constraint CK of Bookmark table
 * Tables: User, SocialGroup, UserGroup, Bookmark, Notification, Place, UserPlace, MemberHashtag, GroupHashtag, Transaction, Reservation
 */

create database kkirikkiri;
select database();
use kkirikkiri;
show tables from kkirikkiri;


-- User Table
CREATE TABLE User (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,				-- real name
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,					-- nickname
    phone_number VARCHAR(20),
    email VARCHAR(255),
    interests TEXT,
    preferred_location VARCHAR(255),
    join_method ENUM('website', 'kakao') NOT NULL,
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
    group_leader_id INT,
    group_join_method ENUM('auto', 'approval') NOT NULL,
    member_limit INT not NULL,
    view_count INT DEFAULT 0,
    bookmark_count INT DEFAULT 0,
    event_date DATETIME not NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint FK_User2SocialGroup_group_leader_id FOREIGN KEY (group_leader_id) REFERENCES User(user_id)
);

-- UserGroup Table (Mapping Table)
CREATE TABLE UserGroup (
    user_group_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    group_id INT,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('pending', 'approved', 'rejected') DEFAULT 'approved',
    constraint FK_User2UserGroup_user_id FOREIGN KEY (user_id) REFERENCES User(user_id),
    constraint FK_SocialGroup2UserGroup_group_id FOREIGN KEY (group_id) REFERENCES SocialGroup(group_id)
);

-- Notification Table
CREATE TABLE Notification (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    message TEXT,
    read_status BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint FK_User2Notification_user_id FOREIGN KEY (user_id) REFERENCES User(user_id)
);

-- Place Table
CREATE TABLE Place (
    place_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) not NULL,
    description TEXT,
    location VARCHAR(255),
    event_date DATETIME not NULL,
    required_members INT not NULL,
    current_members INT DEFAULT 0,
    price DECIMAL(10, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    vendor_id INT,  -- registered company ID
    constraint FK_User2Place_vendor_id FOREIGN KEY (vendor_id) REFERENCES User(user_id)
);

-- UserPlace Table (Mapping Table)
CREATE TABLE UserPlace (
    user_place_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    place_id INT,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('pending', 'confirmed', 'canceled') DEFAULT 'confirmed',
    constraint FK_User2UserPlace_user_id FOREIGN KEY (user_id) REFERENCES User(user_id),
    constraint FK_Place2UserPlace_place_id FOREIGN KEY (place_id) REFERENCES Place(place_id)
);

-- Bookmark Table
CREATE TABLE Bookmark (
    bookmark_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    place_id INT,
    group_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint FK_User2Bookmark_user_id FOREIGN KEY (user_id) REFERENCES User(user_id),
    constraint FK_Place2Bookmark_place_id FOREIGN KEY (place_id) REFERENCES Place(place_id),
    constraint FK_SocialGroup2Bookmark_group_id FOREIGN KEY (group_id) REFERENCES SocialGroup(group_id),
    CONSTRAINT CK_NOTNULL_place_id_OR_group_id CHECK (
        (place_id IS NOT NULL AND group_id IS NULL) OR 
        (place_id IS NULL AND group_id IS NOT NULL) )
);

drop table bookmark;

-- Hashtag Table
CREATE TABLE MemberHashtag (
    hashtag_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    name VARCHAR(50) NOT null,
    constraint FK_User2MemberHashtag_user_id foreign key (user_id) references User(user_id)
);

-- GroupHashtag Table
CREATE TABLE GroupHashtag (
	hashtag_id INT AUTO_INCREMENT PRIMARY key,
    group_id INT,
    name VARCHAR(50) not null,
    FOREIGN KEY (group_id) REFERENCES SocialGroup(group_id)
);

-- Transaction Table for both Socialing, Place
CREATE TABLE Transaction (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    group_id INT,			-- settlement
    leader_id INT,
    place_id INT,			-- payment
    user_id INT,
    amount DECIMAL(10, 2) NOT NULL,
    transaction_type ENUM('PAYMENT', 'SETTLEMENT') NOT NULL,
    status ENUM('PENDING', 'PAID', 'FAILED', 'CANCELLED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    constraint FK_SocialGroup2Transaction_group_id FOREIGN KEY (group_id) REFERENCES SocialGroup(group_id),
    constraint FK_User2Transaction_leader_id FOREIGN KEY (leader_id) REFERENCES User(user_id),
    constraint FK_User2Transaction_user_id FOREIGN KEY (user_id) REFERENCES User(user_id),
    constraint FK_Place2Transaction_place_id FOREIGN KEY (place_id) REFERENCES Place(place_id)
);

-- Reservation Table only for Place
CREATE TABLE Reservation (
    reservation_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    place_id INT,
    reservation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('PENDING', 'CONFIRMED', 'CANCELED') DEFAULT 'PENDING',
--     number_of_people INT,		-- not necessary. only single person can reserve to Place
    price DECIMAL(10, 2),			-- announced price at Place
    event_date DATETIME,			-- announced event date at Place
    payment_status ENUM('PENDING', 'COMPLETED', 'FAILED') DEFAULT 'PENDING',
    constraint FK_User2Reservation_user_id FOREIGN KEY (user_id) REFERENCES User(user_id),
    constraint FK_Place2Reservation_place_id FOREIGN KEY (place_id) REFERENCES Place(place_id)
);