# -- Table definitions

# --- !Ups
CREATE TABLE enquete (
    id int PRIMARY KEY AUTO_INCREMENT,
    name varchar(32) NOT NULL,
    gender varchar(6) NOT NULL,
    message varchar(255) NOT NULL,
    created_at timestamp default CURRENT_TIMESTAMP() NOT NULL
);

CREATE TABLE task (
    id int PRIMARY KEY AUTO_INCREMENT,
    task_id varchar(32) NOT NULL,
    account_id varchar(32) NOT NULL,
    title varchar(32) NOT NULL,
    description varchar(255),
    is_done boolean default FALSE,
    created_at timestamp default CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE account (
    id int AUTO_INCREMENT,
    account_id varchar(32) PRIMARY KEY NOT NULL,
    user_name varchar(255) NOT NULL,
    account_password varchar(255) NOT NULL,
    session_id varchar(255),
    session_timestamp timestamp
);

# --- !Downs
DROP TABLE enquete;

DROP TABLE task;

DROP TABLE account;