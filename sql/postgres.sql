
CREATE TABLE IF NOT EXISTS usertable (
    username VARCHAR(255) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    token VARCHAR(255) DEFAULT NULL,
    coins INT DEFAULT 20,
    name VARCHAR(255) DEFAULT NULL,
    bio VARCHAR(255) DEFAULT NULL,
    image VARCHAR(266) DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS card (
    card_id varchar(255) primary key not null,
    name varchar(255) not null,
    damage float not null,
    monster_type boolean not null,
    element_type varchar(50) not null
);

create table packages (
    id serial primary key,
    card1 varchar(255) not null,
    card2 varchar(255) not null,
    card3 varchar(255) not null,
    card4 varchar(255) not null,
    card5 varchar(255) not null,
    boughtBy varchar(255) DEFAULT null
);

