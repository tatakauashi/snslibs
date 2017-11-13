DROP TABLE IF EXISTS in_last_liked_shortcodes;
CREATE TABLE in_last_liked_shortcodes (
    shortcode varchar(30) NOT NULL,
    revision int NOT NULL,
    account_id varchar(30),
    liked_account_id varchar(30),
    checked_time datetime,
    insert_time datetime,
    PRIMARY KEY last_liked(shortcode, revision),
    KEY `shortcode` (`shortcode`),
    KEY `account_id` (`account_id`),
    KEY `checked_time` (`checked_time`)
);

DROP VIEW IF EXISTS in_last_liked_shortcodes_sub_view;
CREATE VIEW in_last_liked_shortcodes_sub_view AS
select a.shortcode, MAX(a.revision) AS revision FROM in_last_liked_shortcodes a group by a.shortcode;

DROP VIEW IF EXISTS in_last_liked_shortcodes_view;
CREATE VIEW in_last_liked_shortcodes_view AS
SELECT c.shortcode, c.revision, c.account_id, d.username, c.liked_account_id, c.checked_time
FROM in_last_liked_shortcodes_sub_view b
JOIN in_last_liked_shortcodes c ON (b.shortcode = c.shortcode AND b.revision = c.revision)
LEFT JOIN in_accounts d ON (c.account_id = d.account_id)
;
