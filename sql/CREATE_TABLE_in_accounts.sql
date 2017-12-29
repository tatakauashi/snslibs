DROP TABLE IF EXISTS in_accounts;
CREATE TABLE in_accounts (
    account_id varchar(30) primary key,
    username varchar(100),
    profile_pic_url text,
    profile_pic_url_hd text,
    exclution_flag tinyint,
    insert_time datetime,
    update_time datetime
);


INSERT INTO in_accounts
 (account_id, username, exclution_flag, insert_time)
VALUES
 ('4277333839', 'nonnonkanon0811', 1, NOW())
,('4285238306', 'kumakuma9810', 1, NOW())
,('4187465010', 'seiichi_uozumi', 1, NOW())
,('2242196325', 'aigasa_moe', 1, NOW())
;
