/*
 * 2024-10-15
 * CreateTables_11
 * MySQL, DBeaver
 * Update table Place - column category varchar to enum
 * Tables: User, SocialGroup, UserGroup, Interests, Bookmark, Notification, Place, UserPlace, Posts, Photos, Reply MemberHashtag, GroupHashtag, Transaction, Reservation
 */

drop database kkirikkiri;
create database kkirikkiri;
use kkirikkiri;
show tables from kkirikkiri;

-- User Table
CREATE TABLE User (
    user_id VARCHAR(255) PRIMARY KEY,
    username VARCHAR(255),				-- real name
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255),					-- nickname
    birth int,
    gender int,
    phone_number VARCHAR(20),
    email VARCHAR(255),
    preferred_location VARCHAR(255),
    join_method VARCHAR(255),
    profile_image VARCHAR(255),
    user_type ENUM('USER', 'VENDOR') default 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Group Table
CREATE TABLE SocialGroup (
    group_id INT AUTO_INCREMENT PRIMARY KEY,
    group_name VARCHAR(255) NOT NULL,
    description TEXT,
    profile_image VARCHAR(255),
    location VARCHAR(255),
    group_leader_id VARCHAR(255),
    group_join_method ENUM('AUTO', 'APPROVAL') NOT NULL,
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
    user_id VARCHAR(255),
    group_id INT,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'APPROVED',
    constraint FK_User2UserGroup_user_id FOREIGN KEY (user_id) REFERENCES User(user_id),
    constraint FK_SocialGroup2UserGroup_group_id FOREIGN KEY (group_id) REFERENCES SocialGroup(group_id)
);

-- Interests Table
create table Interest (
	interest_id int auto_increment primary key,
	user_id VARCHAR(255),
	interest VARCHAR(255),
	constraint FK_User2Interests_user_id foreign key (user_id) references User(user_id)
);

-- Notification Table
CREATE TABLE Notification (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255),
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
    profile_image VARCHAR(255),
    category ENUM('HOBBY', 'EXHIBITION', 'SHOW', 'EVENT', 'SPACE') not null,
    location VARCHAR(255),
    event_date DATETIME not NULL,
    required_members INT not NULL,
    current_members INT DEFAULT 0,
    member_limit INT not null,
    view_count INT default 0,
    bookmark_count INT default 0,
    price DECIMAL(10, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    vendor_id VARCHAR(255),  -- registered company ID
    constraint FK_User2Place_vendor_id FOREIGN KEY (vendor_id) REFERENCES User(user_id)
);

-- UserPlace Table (Mapping Table)
CREATE TABLE UserPlace (
    user_place_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255),
    place_id INT,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('PENDING', 'CONFIRMED', 'CANCELED') DEFAULT 'CONFIRMED',
    constraint FK_User2UserPlace_user_id FOREIGN KEY (user_id) REFERENCES User(user_id),
    constraint FK_Place2UserPlace_place_id FOREIGN KEY (place_id) REFERENCES Place(place_id)
);

-- Posts Table
create table Post (
	post_id int auto_increment primary key,
	group_id int,
	place_id int,
	user_id VARCHAR(255),
	content TEXT,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	post_type ENUM('GENERAL', 'NOTIFICATION') not null,
	constraint FK_User2Posts_user_id foreign key (user_id) references User(user_id),
	constraint FK_SocialGroup2Posts_group_id foreign key (group_id) references SocialGroup(group_id),
	constraint FK_Place2Posts_user_id foreign key (place_id) references Place(place_id),
	CONSTRAINT CK_Posts_NOTNULL_place_id_OR_group_id CHECK (
        (place_id IS NOT NULL AND group_id IS NULL) OR 
        (place_id IS NULL AND group_id IS NOT NULL) )
);

-- Photos Table
create table Photo (
	photo_id int auto_increment primary key,
	post_id int,
	image_name VARCHAR(255),
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	constraint FK_Post2Photos_post_id foreign key (post_id) references Post(post_id)
);

-- Reply Table
create table Reply (
	reply_id int auto_increment primary key,
	post_id int,
	user_id VARCHAR(255),
	content VARCHAR(255),
	created_at TIMESTAMP default CURRENT_TIMESTAMP,
	constraint FK_Post2Reply_post_id foreign key (post_id) references Post(post_id),
	constraint FK_User2Reply_user_id foreign key (user_id) references User(user_id)
);

-- Bookmark Table
CREATE TABLE Bookmark (
    bookmark_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255),
    place_id INT,
    group_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint FK_User2Bookmark_user_id FOREIGN KEY (user_id) REFERENCES User(user_id),
    constraint FK_Place2Bookmark_place_id FOREIGN KEY (place_id) REFERENCES Place(place_id),
    constraint FK_SocialGroup2Bookmark_group_id FOREIGN KEY (group_id) REFERENCES SocialGroup(group_id),
    CONSTRAINT CK_Bookmark_NOTNULL_place_id_OR_group_id CHECK (
        (place_id IS NOT NULL AND group_id IS NULL) OR 
        (place_id IS NULL AND group_id IS NOT NULL) )
);


-- Hashtag Table
CREATE TABLE MemberHashtag (
    hashtag_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255),
    name VARCHAR(50) NOT null,
    constraint FK_User2MemberHashtag_user_id foreign key (user_id) references User(user_id)
);

-- GroupHashtag Table
CREATE TABLE GroupHashtag (
	hashtag_id INT AUTO_INCREMENT PRIMARY key,
    group_id INT,
    name VARCHAR(50) not null,
    constraint FK_SocialGroup2GroupHashtag FOREIGN KEY (group_id) REFERENCES SocialGroup(group_id)
);

-- Transaction Table for both Socialing, Place
CREATE TABLE Transaction (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    group_id INT,			-- settlement
    leader_id VARCHAR(255),
    place_id INT,			-- payment
    user_id VARCHAR(255),
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
    user_id VARCHAR(255),
    place_id INT,
    reservation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('PENDING', 'CONFIRMED', 'CANCELED') DEFAULT 'PENDING',
    price DECIMAL(10, 2),			-- announced price at Place
    event_date DATETIME,			-- announced event date at Place
    payment_status ENUM('PENDING', 'COMPLETED', 'FAILED') DEFAULT 'PENDING',
    constraint FK_User2Reservation_user_id FOREIGN KEY (user_id) REFERENCES User(user_id),
    constraint FK_Place2Reservation_place_id FOREIGN KEY (place_id) REFERENCES Place(place_id)
);