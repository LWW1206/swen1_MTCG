
CREATE TABLE IF NOT EXISTS usertable (
    username VARCHAR(255) PRIMARY KEY,
    password VARCHAR(255) not null,
    token VARCHAR(255) default null,
    coins INT DEFAULT 20,
    name VARCHAR(255) default null,
    bio VARCHAR(255) default null,
    image VARCHAR(266) default null,
);

CREATE TABLE IF NOT EXISTS card (
    card_id varchar(255) primary key not null,
    name varchar(255) not null,
    damage float not null,
    monster_type boolean not null,
    element_type varchar(255) not null,
    owner varchar(255)
);

create table packages (
    id serial primary key,
    card1 varchar(255) not null,
    card2 varchar(255) not null,
    card3 varchar(255) not null,
    card4 varchar(255) not null,
    card5 varchar(255) not null,
    boughtBy varchar(255) default null
);

create table deck (
    username varchar(255) not null unique,
    card1id varchar(255),
    card2id varchar(255),
    card3id varchar(255),
    card4id varchar(255)
);

create table stats (
    username varchar(255) not null,
    games_played int default 0 not null,
    games_won int default 0 not null,
    games_lost int default 0 not null,
    elo int default 100 not null,
    FOREIGN KEY (username) references usertable(username)
);

create table tradingdeal (
    tradeid varchar(255) not null,
    cardid varchar(255) not null,
    type varchar(255) not null,
    minDmg int,
    element varchar(255)
);


