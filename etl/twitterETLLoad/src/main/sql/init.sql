# sudo mysql

use mysql;
create user 'clouduser'@'localhost' identified by 'dbroot';
GRANT ALL PRIVILEGES ON *.* TO 'clouduser'@'localhost' WITH GRANT OPTION;
create user 'clouduser'@'%' identified by 'dbroot';
GRANT ALL PRIVILEGES ON *.* TO 'clouduser'@'%' WITH GRANT OPTION;
GRANT FILE ON *.* TO 'clouduser'@'localhost';
GRANT FILE ON *.* TO 'clouduser'@'%';

SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;

create database twitter_db DEFAULT CHARSET = utf8mb4 DEFAULT COLLATE = utf8mb4_unicode_ci;
use twitter_db;

CREATE TABLE CompleteTweets (
    tweet_id BIGINT,
    user_id BIGINT,
    text TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    entities_hashtags TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    tweet_created_at DATETIME,
    reply_id BIGINT,
    retweet_id BIGINT,
    sender_screen_name VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    sender_description TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    sender_hashtag_counts TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    contact_screen_name VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    contact_description TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    contact_hashtag_counts TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci;

