DROP TABLE IF EXISTS customer;
CREATE TABLE customer (
    tw_user_id           varchar(100)  PRIMARY KEY,
    screen_name          varchar(100)  NOT NULL,
    description          text,
    access_token         varchar(1024) NOT NULL,
    access_token_secret  varchar(1024) NOT NULL,
    regist_time          datetime      NOT NULL,
    update_time          datetime      NOT NULL,
    enable_flag          tinyint       NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8
;


DROP TABLE IF EXISTS tweet_template;
CREATE TABLE tweet_template (
    tw_user_id      varchar(100) NOT NULL,
    in_account_id   varchar(100) NOT NULL,
    revision        int          NOT NULL DEFAULT 1,
    template        text         NOT NULL,
    regist_time     datetime     NOT NULL,
    update_time     datetime     NOT NULL,
    enable_flag     tinyint      NOT NULL DEFAULT 0,
    PRIMARY KEY tweet_template_pkey(tw_user_id, in_account_id, revision)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
;
