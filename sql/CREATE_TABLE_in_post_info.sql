DROP TABLE IF EXISTS in_post_info;
CREATE TABLE in_post_info (
    shortcode varchar(30) NOT NULL,
    account_id varchar(30) NOT NULL,
    post_text text,
    display_url_json text,
    taken_at_time datetime,
    insert_time datetime,
    PRIMARY KEY `shortcode` (`shortcode`),
    KEY `account_id` (`account_id`),
    KEY `taken_at_time` (`taken_at_time`)
) DEFAULT CHARACTER SET utf8mb4;


DROP TABLE IF EXISTS in_post_add_info;
CREATE TABLE in_post_add_info (
    shortcode varchar(30) NOT NULL,
    revision int NOT NULL,
    price int,
    description text,
    detail_url_json text,
    insert_time datetime,
    PRIMARY KEY `post_add_pk` (`shortcode`, `revision`),
    KEY `price` (`price`)
) DEFAULT CHARACTER SET utf8mb4;

DROP VIEW IF EXISTS in_post_add_info_sub_view;
CREATE VIEW in_post_add_info_sub_view AS
select a.shortcode, MAX(a.revision) AS revision FROM in_last_liked_shortcodes a group by a.shortcode;

DROP VIEW IF EXISTS in_post_add_info_view;
CREATE VIEW in_post_add_info_view AS
SELECT c.shortcode, c.revision, e.username, c.price, c.description, c.detail_url_json, d.taken_at_time
FROM in_post_add_info_sub_view b
JOIN in_post_add_info c ON (b.shortcode = c.shortcode AND b.revision = c.revision)
JOIN in_liked_shortcodes d ON (c.shortcode = d.shortcode)
LEFT JOIN in_accounts e ON (d.account_id = e.account_id)
;
