create database RevConnect;
use RevConnect;
show tables;
drop table comments, connections, followers, likes, notifications, posts, users;

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,

    user_type ENUM('PERSONAL', 'CREATOR', 'BUSINESS') DEFAULT 'PERSONAL',

    name VARCHAR(100),
    bio VARCHAR(300),
    location VARCHAR(100),
    website VARCHAR(150),
    profile_pic VARCHAR(200),

    is_private BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE posts (
    post_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    content VARCHAR(500) NOT NULL,
    hashtags VARCHAR(200),
    is_promotional BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE comments (
    comment_id INT AUTO_INCREMENT PRIMARY KEY,
    post_id INT NOT NULL,
    user_id INT NOT NULL,
    comment_text VARCHAR(300) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE likes (
    like_id INT AUTO_INCREMENT PRIMARY KEY,
    post_id INT NOT NULL,
    user_id INT NOT NULL,
    FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE (post_id, user_id)
);

CREATE TABLE connections (
    connection_id INT AUTO_INCREMENT PRIMARY KEY,
    sender_id INT NOT NULL,
    receiver_id INT NOT NULL,

    status ENUM('PENDING', 'ACCEPTED', 'REJECTED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE (sender_id, receiver_id)
);

drop table connections;
CREATE TABLE connections (
    connection_id INT AUTO_INCREMENT PRIMARY KEY,
    sender_id INT,
    receiver_id INT,
    status VARCHAR(20) DEFAULT 'PENDING',
    FOREIGN KEY (sender_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES users(user_id) ON DELETE CASCADE
);

drop table followers;
CREATE TABLE followers (
    follower_user_id INT,
    following_user_id INT,
    PRIMARY KEY(follower_user_id, following_user_id),
    FOREIGN KEY (follower_user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (following_user_id) REFERENCES users(user_id) ON DELETE CASCADE
);


CREATE TABLE notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    message VARCHAR(500),
    notification_type ENUM(
        'LIKE',
        'COMMENT',
        'FOLLOW',
        'CONNECTION_REQUEST',
        'CONNECTION_ACCEPTED',
        'POST'),
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);


select * from followers;
use RevConnect;
show tables;
select * from users;
select * from posts;
select * from likes;
select * from connections;
select * from comments;
select * from profiles;
select * from notifications;
truncate table likes;

desc posts;

ALTER TABLE notifications
ADD COLUMN sender_username VARCHAR(100) AFTER user_id;


CREATE TABLE profiles (
    profile_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNIQUE,
    full_name VARCHAR(100),
    bio TEXT,
    profile_pic VARCHAR(255),
    location VARCHAR(100),
    website VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

select * from posts;

CREATE TABLE connections (
    connection_id INT AUTO_INCREMENT PRIMARY KEY,
    sender_id INT NOT NULL,
    receiver_id INT NOT NULL,
    status ENUM('PENDING', 'ACCEPTED', 'REJECTED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE (sender_id, receiver_id)
);

use RevConnect;
drop table connections;

SELECT post_id, content
FROM posts
WHERE user_id = (
    SELECT user_id FROM users WHERE username = 'Nita12'
)
ORDER BY post_id;

SELECT user_id, username 
FROM users
WHERE username IS NULL
   OR username = ''
   OR username REGEXP '^[0-9]+$';








