create table if not exists currencies
(
    id        integer primary key autoincrement,
    code      varchar(255) unique,
    full_name varchar(255) unique,
    sign      varchar(4) unique
);


create table if not exists exchange_rates
(
    id                 integer primary key autoincrement,
    base_currency_id   integer not null,
    target_currency_id integer not null,
    rate               decimal(6),
    foreign key (base_currency_id) references currencies (id),
    foreign key (target_currency_id) references currencies (id)
);