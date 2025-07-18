create table k_member
(
    id         bigint auto_increment primary key,
    user_id    varchar(9)                          not null unique,
    username   varchar(50)                         not null,
    barcode    varchar(10)                         null unique,
    created_at timestamp default current_timestamp not null
);