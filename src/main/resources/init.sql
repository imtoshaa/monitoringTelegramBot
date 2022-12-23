drop schema if exists bot_monitoring;
create schema if not exists bot_monitoring;

drop table if exists bot_monitoring.users;
create table if not exists bot_monitoring.users
(
    id            serial unique,
    serial_number varchar(5) unique,
    password      varchar(200),
    name          varchar(200),
    surname       varchar(200),
    chat_id        long,
    primary key (id)
);

drop table if exists bot_monitoring.days;
create table if not exists bot_monitoring.days
(
    id   serial unique,
    date varchar(200),
    primary key (id)
);

drop table if exists bot_monitoring.events;
create table if not exists bot_monitoring.events
(
    id          serial unique,
    event_time        varchar(8),
    car_number  int,
    description varchar(1000),
    image_path  varchar(500),
    day_id      bigint unsigned,
    user_id     bigint unsigned,
    constraint fk_events_day_id_days_id
        foreign key (day_id) references bot_monitoring.days (id) on update cascade,
    constraint fk_events_user_id_user_id
        foreign key (user_id) references bot_monitoring.users (id) on update cascade

);

insert into bot_monitoring.users (serial_number, name, surname)
    value (
           43600,
           'Дмитрий',
           'Елисеев'
    );