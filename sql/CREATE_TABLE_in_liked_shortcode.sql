DROP TABLE IF EXISTS in_liked_shortcodes;
CREATE TABLE in_liked_shortcodes (
    shortcode varchar(30) PRIMARY KEY,
    account_id varchar(30),
    taken_at_time datetime,
    tweet_flag tinyint,
    tweet_time datetime,
    insert_time datetime,
    KEY `account_id` (`account_id`)
);
