create table k_member_point
(
    id             bigint auto_increment primary key,
    barcode        varchar(10)   not null,
    store_category bigint        not null,
    point          int default 0 null,
    created_at     timestamp default current_timestamp not null,
    updated_at     timestamp     null
);