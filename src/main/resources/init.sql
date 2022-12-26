drop schema if exists bot_monitoring;
create schema if not exists bot_monitoring;

drop table if exists bot_monitoring.roles;
create table if not exists bot_monitoring.roles
(
    id   serial unique,
    role varchar(25) unique not null,
    primary key (id),
    unique index idx_roles_id_unique (id asc),
    unique index idx_roles_role_unique (role asc)
);

drop table if exists bot_monitoring.users;
create table if not exists bot_monitoring.users
(
    id            serial unique,
    serial_number varchar(5) unique,
    password      varchar(200),
    name          varchar(200),
    surname       varchar(200),
    chat_id       long,
    role_id       bigint unsigned not null,
    primary key (id),
        constraint fk_users_role_id_roles_id
        foreign key (role_id) references bot_monitoring.roles (id) on update cascade
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
    event_time  varchar(8),
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

insert into bot_monitoring.roles (role) value ('admin');
insert into bot_monitoring.roles (role) value ('client');

insert into bot_monitoring.users (serial_number, password, name, surname, role_id)
    value (
           43600,
           '$2a$08$xJI6QJL5Wh8uKaK.HbQ7e.4Gd5y/k3Q6nbkx9TQU3vMhFNY30/jr.',
           'Дмитрий',
           'Елисеев',
          1
    );
insert into bot_monitoring.users (serial_number, password, name, surname, role_id)
    value (
           56856,
           '$2a$08$xJI6QJL5Wh8uKaK.HbQ7e.4Gd5y/k3Q6nbkx9TQU3vMhFNY30/jr.',
           'Антон',
           'Овсейчик',
           1
    );