CREATE DATABASE secureweb;
use secureweb;
CREATE TABLE users (
                       id int NOT NULL AUTO_INCREMENT,
                       username varchar(50) NOT NULL,
                       password varchar(255) NOT NULL,
                       role enum('user','admin') NOT NULL,
                       profile_picture varchar(255) DEFAULT 'default.jpg',
                       PRIMARY KEY (id),
                       UNIQUE KEY username (username)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE messages (id INT NOT NULL AUTO_INCREMENT,username VARCHAR(50) NOT NULL,message TEXT NOT NULL, date_posted TIMESTAMP DEFAULT CURRENT_TIMESTAMP,PRIMARY KEY (id)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


DELIMITER $$

CREATE TRIGGER before_users_insert
    BEFORE INSERT ON users
    FOR EACH ROW
BEGIN
    -- Check if the password is already hashed
    IF LENGTH(NEW.password) != 64 THEN
        SET NEW.password = SHA2(NEW.password, 256);
END IF;
END$$

DELIMITER ;



DELIMITER $$

CREATE TRIGGER before_users_update
    BEFORE UPDATE ON users
    FOR EACH ROW
BEGIN
    -- Only hash the password if it has changed and is not already hashed
    IF LENGTH(NEW.password) != 64 THEN
        SET NEW.password = SHA2(NEW.password, 256);
END IF;
END$$

DELIMITER ;


INSERT INTO users (username, password, role, profile_picture)
VALUES ('admin', 'admin', 'admin', 'default.jpg');

INSERT INTO users (username, password, role, profile_picture)
VALUES ('vafa', 'vafa', 'user', 'vafa.jpg');

INSERT INTO users (username, password, role, profile_picture)
VALUES ('nargiz', 'nargiz', 'user', 'nargiz.jpg');

INSERT INTO users (username, password, role, profile_picture)
VALUES ('sevinj', 'sevinj', 'user', 'default.jpg');