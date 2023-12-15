CREATE TABLE IF NOT EXISTS cards (
    card_id varchar(255) primary key not null,
    name varchar(255) not null,
    damage float not null
);