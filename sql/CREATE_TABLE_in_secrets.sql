DROP TABLE IF EXISTS in_secrets;
CREATE TABLE in_secrets (
    account_id varchar(30) primary key,
    insert_time datetime,
    update_time datetime,
    deleted_flag tinyint DEFAULT 0,
    KEY `account_id` (`account_id`)
);


INSERT INTO in_secrets
 (account_id, insert_time, update_time)
VALUES
 ('2421050',   NOW(), NOW())  -- gucci
,('187619120', NOW(), NOW())  -- louisvuitton
;
